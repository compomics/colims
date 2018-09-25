package com.compomics.colims.core.playground;

import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.io.fasta.FastaDbParser;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.Protein;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(final String[] args) throws IOException, ClassNotFoundException, SQLException {
        ApplicationContextProvider.getInstance().setDefaultApplicationContext();
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();
        //
        //        SchemaExport schemaExport = new SchemaExport(sessionFactoryBean.getConfiguration());
        //        schemaExport.setOutputFile("C:\\Users\\niels\\Desktop\\testing.txt");
        //        schemaExport.setFormat(true);
        //        schemaExport.setDelimiter(";");
        //        schemaExport.execute(true, false, false, true);
        //        CpsParentImpl cpsParentImpl = new CpsParentImpl();
        //        cpsParentImpl.setCpsFile(new File("C:\\Users\\niels\\Desktop\\test\\data/peptideshaker/test_peptideshaker_project.cps"));
        //        cpsParentImpl.loadCpsFile(null);
        //        cpsParentImpl.loadSpectrumFiles(null);
        //        System.out.println("test");

//        Query olsClient = (Query) applicationContext.getBean("olsClient");
//        Map ontologyNames = olsClient.getOntologyNames();
//        for (MapItem mapItem : ontologyNames.getItem()) {
//            if (mapItem.getKey().equals("MS")) {
//                System.out.println(mapItem.getKey());
//                System.out.println(mapItem.getValue());
//            }
//        }
//        Map termsByName = olsClient.getTermsByName("oxidation of m", "MOD", false);  
//        Map termsByExactName = olsClient.getTermsByExactName("methionine oxidation with neutral loss of 64 Da", "MOD");  
//        Map termXrefs = olsClient.getTermXrefs("MOD:00935", "MOD");
//        Map termMetadata = olsClient.getTermMetadata("MOD:00935", "MOD");
//        
//        List<DataHolder> termsByAnnotationData = olsClient.getTermsByAnnotationData("MOD", "DiffMono", null, 15.894915000000001, 16.094915);

        FastaDbParser fastaDbParser = (FastaDbParser) applicationContext.getBean("fastaDbParser");
        FastaDb testFastaDb = new FastaDb();
        testFastaDb.setName("test fasta");
        testFastaDb.setFileName("SP_human.fasta");
        testFastaDb.setFilePath(Paths.get("/home/niels/Desktop/fastas/SP_human.fasta").toString());
        testFastaDb.setHeaderParseRule("&gt;.*\\|(.*)\\|");
        testFastaDb.setVersion("N/A");
        testFastaDb.setDatabaseName("test db");
        LinkedHashMap<FastaDb, Path> fastaDbs = new LinkedHashMap<>();
        fastaDbs.put(testFastaDb, Paths.get(testFastaDb.getFilePath()));
        Map<String, Protein> parse = fastaDbParser.parse(fastaDbs);
        Protein protein = parse.get("Q5VTE0");
        System.out.println("=---------------");

    }
}
