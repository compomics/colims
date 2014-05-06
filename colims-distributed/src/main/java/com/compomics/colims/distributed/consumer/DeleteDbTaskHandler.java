package com.compomics.colims.distributed.consumer;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.MaxQuantDataImport;
import com.compomics.colims.core.io.maxquant.MaxQuantImportMapper;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerDataImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImportMapper;
import com.compomics.colims.core.io.peptideshaker.UnpackedPsDataImport;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.model.CompletedDbTask;
import com.compomics.colims.distributed.model.DbTaskError;
import com.compomics.colims.distributed.model.DeleteDbTask;
import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.producer.CompletedTaskProducer;
import com.compomics.colims.distributed.producer.DbTaskErrorProducer;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Sample;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("deleteDbTaskHandler")
public class DeleteDbTaskHandler {

    private static final Logger LOGGER = Logger.getLogger(DeleteDbTaskHandler.class);

    @Autowired
    private CompletedTaskProducer completedTaskProducer;
    @Autowired
    private DbTaskErrorProducer dbTaskErrorProducer;
    @Autowired
    private PeptideShakerIO peptideShakerIO;
    @Autowired
    private PeptideShakerImportMapper peptideShakerImportMapper;
    @Autowired
    private MaxQuantImportMapper maxQuantImportMapper;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private UserService userService;
    @Autowired
    private SampleService sampleService;

    public void handleDeleteDbTask(DeleteDbTask deleteDbTask) {
        try {
            Long started = System.currentTimeMillis();
                        
            //wrap the DeleteDbTask in a CompletedTask and send it to the completed task queue
            completedTaskProducer.sendCompletedDbTask(new CompletedDbTask(started, System.currentTimeMillis(), deleteDbTask));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //wrap the DeleteDbTask in a DbTaskError and send it to the error queue
            dbTaskErrorProducer.sendDbTaskError(new DbTaskError(deleteDbTask, e));
        }
    }
    
}
