package com.compomics.colims.distributed.consumer;

import com.compomics.colims.core.distributed.model.CompletedDbTask;
import com.compomics.colims.core.distributed.model.DbTaskError;
import com.compomics.colims.core.distributed.model.Notification;
import com.compomics.colims.core.distributed.model.PersistDbTask;
import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.io.PeptideShakerImport;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.PersistService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.io.maxquant.MaxQuantMapper;
import com.compomics.colims.distributed.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.distributed.io.peptideshaker.PeptideShakerMapper;
import com.compomics.colims.distributed.io.peptideshaker.UnpackedPeptideShakerImport;
import com.compomics.colims.distributed.producer.CompletedTaskProducer;
import com.compomics.colims.distributed.producer.DbTaskErrorProducer;
import com.compomics.colims.distributed.producer.NotificationProducer;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Sample;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * This class handles a PersistDbTask: map the DataImport en store it in the
 * database.
 *
 * @author Niels Hulstaert
 */
@Component("persistDbTaskHandler")
public class PersistDbTaskHandler {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(PersistDbTaskHandler.class);

    private static final String STARTED_MESSAGE = "started parsing...  ";
    private static final String FINISHED_MESSAGE = "finished parsing ";
    private static final String DB_STARTED_MESSAGE = "saving to database...";
    /**
     * The experiments location as provided in the distributed properties file.
     */
    @Value("${experiments.path}")
    private String experimentsPath = "";
    /**
     * The FASTA DBs location as provided in the distributed properties file.
     */
    @Value("${fastas.path}")
    private String fastasPath = "";
    /**
     * The CompletedDbTask sender.
     */
    private final CompletedTaskProducer completedTaskProducer;
    /**
     * The DbTaskError sender.
     */
    @Autowired
    private final DbTaskErrorProducer dbTaskErrorProducer;
    /**
     * The PeptideShaker IO handler.
     */
    @Autowired
    private final PeptideShakerIO peptideShakerIO;
    /**
     * The PeptideShaker data mapper.
     */
    @Autowired
    private final PeptideShakerMapper peptideShakerMapper;
    /**
     * The MaxQuant data mapper.
     */
    @Autowired
    private final MaxQuantMapper maxQuantMapper;
    /**
     * The user entity service.
     */
    @Autowired
    private final UserService userService;
    /**
     * The sample entity service.
     */
    @Autowired
    private final SampleService sampleService;
    /**
     * The instrument entity service.
     */
    @Autowired
    private final InstrumentService instrumentService;
    /**
     * The persist service
     */
    @Autowired
    private final PersistService persistService;
    /**
     * The Notification message sender.
     */
    @Autowired
    private final NotificationProducer notificationProducer;

    @Autowired
    public PersistDbTaskHandler(CompletedTaskProducer completedTaskProducer,
                                DbTaskErrorProducer dbTaskErrorProducer,
                                PeptideShakerIO peptideShakerIO,
                                PeptideShakerMapper peptideShakerMapper,
                                MaxQuantMapper maxQuantMapper,
                                UserService userService,
                                SampleService sampleService,
                                InstrumentService instrumentService,
                                PersistService persistService,
                                NotificationProducer notificationProducer) {
        this.completedTaskProducer = completedTaskProducer;
        this.dbTaskErrorProducer = dbTaskErrorProducer;
        this.peptideShakerIO = peptideShakerIO;
        this.peptideShakerMapper = peptideShakerMapper;
        this.maxQuantMapper = maxQuantMapper;
        this.userService = userService;
        this.sampleService = sampleService;
        this.instrumentService = instrumentService;
        this.persistService = persistService;
        this.notificationProducer = notificationProducer;
    }

    public void handlePersistDbTask(PersistDbTask persistDbTask) {
        Long started = System.currentTimeMillis();
        try {
            //check if the entity class is of the right type
            if (!persistDbTask.getDbEntityClass().equals(AnalyticalRun.class)) {
                throw new IllegalArgumentException("The entity to persist should be of class " + AnalyticalRun.class.getName());
            }

            //get the sample
            Sample sample = sampleService.findById(persistDbTask.getEnitityId());
            if (sample == null) {
                throw new IllegalArgumentException("The sample entity with ID " + persistDbTask.getEnitityId() + " was not found in the database.");
            }

            //get the instrument
            Instrument instrument = instrumentService.findById(persistDbTask.getPersistMetadata().getInstrumentId());
            if (instrument == null) {
                throw new IllegalArgumentException("The instrument with ID " + persistDbTask.getPersistMetadata().getInstrumentId() + " was not found in the database.");
            }

            //get the user name for auditing
            String userName = userService.findUserNameById(persistDbTask.getUserId());
            if (userName == null) {
                throw new IllegalArgumentException("The user with ID " + persistDbTask.getUserId() + " was not found in the database.");
            }

            //map the task
            MappedData mappedData = mapDataImport(persistDbTask);
            notificationProducer.sendNotification(new Notification(DB_STARTED_MESSAGE, ""));
            persistService.persist(mappedData, sample, instrument, userName, persistDbTask.getPersistMetadata().getStartDate());

            //wrap the PersistDbTask in a CompletedTask and send it to the completed task queue
            completedTaskProducer.sendCompletedDbTask(new CompletedDbTask(started, System.currentTimeMillis(), persistDbTask));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //wrap the StorageTask in a StorageError and send it to the error queue
            try {
                dbTaskErrorProducer.sendDbTaskError(new DbTaskError(started, System.currentTimeMillis(), persistDbTask, e));
            } catch (IOException e1) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Map the persist db task.
     *
     * @param persistDbTask the persist task containing the DataImport object
     * @return the MappedDataImport instance
     * @throws MappingException                                       thrown in case of a mapping exception
     * @throws java.io.IOException                                    thrown in case of an IO related problem
     * @throws org.apache.commons.compress.archivers.ArchiveException thrown in case of an Archiver exception
     * @throws java.lang.ClassNotFoundException                       thrown in case of a failure to load a class by
     *                                                                it's string name
     * @throws java.sql.SQLException                                  thrown in case of an SQL related problem
     * @throws InterruptedException                                   thrown in case a thread is interrupted
     */
    private MappedData mapDataImport(PersistDbTask persistDbTask) throws MappingException, IOException, ArchiveException, ClassNotFoundException, SQLException, InterruptedException, JDOMException {
        MappedData mappedData = null;

        notificationProducer.sendNotification(new Notification(STARTED_MESSAGE, ""));

        //check if the experiments and FASTA DBs locations exist
        Path experimentsDirectory = Paths.get(this.experimentsPath);
        if (!Files.exists(experimentsDirectory)) {
            throw new IllegalArgumentException("The experiments directory " + this.experimentsPath + " doesn't exist.");
        }
        Path fastasDirectory = Paths.get(this.fastasPath);
        if (!Files.exists(fastasDirectory)) {
            throw new IllegalArgumentException("The FASTA DBs directory " + this.fastasPath + " doesn't exist.");
        }

        switch (persistDbTask.getPersistMetadata().getPersistType()) {
            case PEPTIDESHAKER:
                PeptideShakerImport peptideShakerImport = (PeptideShakerImport) (persistDbTask.getDataImport());

                //unpack .cps archive
                UnpackedPeptideShakerImport unpackedPeptideShakerImport = peptideShakerIO.unpackPeptideShakerImport(peptideShakerImport, experimentsDirectory);

                mappedData = peptideShakerMapper.mapData(unpackedPeptideShakerImport, experimentsDirectory, fastasDirectory);

                //clear resources after mapping
                peptideShakerMapper.clear();

                //try to delete the temporary directory with the unpacked .cps file
                try {
                    FileUtils.deleteDirectory(unpackedPeptideShakerImport.getUnpackedDirectory());
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                }
//                if (unpackedPeptideShakerImport.getUnpackedDirectory().exists()) {
//                    LOGGER.warn("The directory " + unpackedPeptideShakerImport.getDbDirectory() + " could not be deleted.");
//                }
                break;
            case MAX_QUANT:
                mappedData = maxQuantMapper.mapData((MaxQuantImport) (persistDbTask.getDataImport()), experimentsDirectory, fastasDirectory);

                //clear resources after mapping
                maxQuantMapper.clear();

                break;
            default:
                break;
        }
        notificationProducer.sendNotification(new Notification(FINISHED_MESSAGE, ""));
        return mappedData;
    }
}
