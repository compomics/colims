package com.compomics.colims.client.playground;

import com.compomics.colims.core.io.MappingException;

import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.IOException;
import java.sql.SQLException;
import org.hibernate.boot.archive.spi.ArchiveException;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(String[] args) throws IOException, MappingException, SQLException, ClassNotFoundException, InterruptedException, IllegalArgumentException, MzMLUnmarshallerException, XmlPullParserException, ArchiveException {

//        ObjectMapper objectMapper = new ObjectMapper();
//
//        final PersistDbTask persistDbTask = new PersistDbTask();
//        persistDbTask.setDbEntityClass(AnalyticalRun.class);
//        persistDbTask.setMessageId("1234567");
//        persistDbTask.setEnitityId(1L);
//        persistDbTask.setSubmissionTimestamp(Long.MIN_VALUE);
//        persistDbTask.setSubmissionTimestamp(System.currentTimeMillis());
//
//        PersistMetadata persistMetadata = new PersistMetadata();
//        persistMetadata.setDescription("test description");
//        persistMetadata.setPersistType(PersistType.PEPTIDESHAKER);
//        persistMetadata.setInstrumentId(1L);
//        persistMetadata.setStartDate(new Date());
//        persistDbTask.setPersistMetadata(persistMetadata);
//
//        List<Path> mgfFiles = Arrays.asList(Paths.get("maxquant_test1"), Paths.get("test2"));
//        EnumMap<FastaDbType, List<Long>> fastaDbIds = new EnumMap<>(FastaDbType.class);
//        fastaDbIds.put(FastaDbType.PRIMARY,new ArrayList<>(Arrays.asList(1L)));
//        DataImport dataImport = new PeptideShakerImport(Paths.get("testFile"), fastaDbIds, mgfFiles);
//        persistDbTask.setDataImport(dataImport);
//
//        DbTask dbTask = persistDbTask;
//
//        String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dbTask);
//
//        PersistDbTask persistDbTask1 = objectMapper.readValue(s, PersistDbTask.class);

        String test = "blbaljjs";
        String[] split = test.split(";");

        System.out.println("s");
    }
}
