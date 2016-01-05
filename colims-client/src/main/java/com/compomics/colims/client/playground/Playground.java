package com.compomics.colims.client.playground;

import com.compomics.colims.core.distributed.model.DbTask;
import com.compomics.colims.core.distributed.model.PersistDbTask;
import com.compomics.colims.core.distributed.model.PersistMetadata;
import com.compomics.colims.core.distributed.model.enums.PersistType;
import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.PeptideShakerImport;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.enums.FastaDbType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.compress.archivers.ArchiveException;
import org.xmlpull.v1.XmlPullParserException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(String[] args) throws IOException, MappingException, SQLException, ClassNotFoundException, InterruptedException, IllegalArgumentException, MzMLUnmarshallerException, XmlPullParserException, ArchiveException {

        ObjectMapper objectMapper = new ObjectMapper();

        final PersistDbTask persistDbTask = new PersistDbTask();
        persistDbTask.setDbEntityClass(AnalyticalRun.class);
        persistDbTask.setMessageId("1234567");
        persistDbTask.setEnitityId(1L);
        persistDbTask.setSubmissionTimestamp(Long.MIN_VALUE);
        persistDbTask.setSubmissionTimestamp(System.currentTimeMillis());

        PersistMetadata persistMetadata = new PersistMetadata();
        persistMetadata.setDescription("test description");
        persistMetadata.setPersistType(PersistType.PEPTIDESHAKER);
        persistMetadata.setInstrumentId(1L);
        persistMetadata.setStartDate(new Date());
        persistDbTask.setPersistMetadata(persistMetadata);

        List<File> mgfFiles = Arrays.asList(new File("test1"), new File("test2"));
        EnumMap<FastaDbType, Long> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, 1L);
        DataImport dataImport = new PeptideShakerImport(new File("testFile"), fastaDbIds, mgfFiles);
        persistDbTask.setDataImport(dataImport);

        DbTask dbTask = persistDbTask;

        String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dbTask);

        PersistDbTask persistDbTask1 = objectMapper.readValue(s, PersistDbTask.class);

        System.out.println(s);
        System.out.println("jjjjjjjjj");

    }
}
