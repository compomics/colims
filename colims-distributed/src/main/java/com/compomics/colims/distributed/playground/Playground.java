
package com.compomics.colims.distributed.playground;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.io.maxquant.MaxQuantMapper;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantSpectraParser;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    @Autowired
    static MaxQuantMapper maxQuantMapper;
    
    @Autowired
    static MaxQuantSpectraParser maxQuantSpectraParser;
    
    @Autowired
    static AnnotatedSpectraParser annotatedSpectraParser;

    public static void main(String[] args) throws MappingException, JDOMException, IOException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-distributed-context.xml");

        MaxQuantMapper maxQuantMapper = applicationContext.getBean("maxQuantMapper", MaxQuantMapper.class);
        MaxQuantSpectraParser maxQuantSpectraParser = applicationContext.getBean("maxQuantSpectraParser", MaxQuantSpectraParser.class);
        AnnotatedSpectraParser annotatedSpectraParser = applicationContext.getBean("annotatedSpectraParser", AnnotatedSpectraParser.class);
        UserBean userBean = applicationContext.getBean("userBean", UserBean.class);
        UserService userService = applicationContext.getBean("userService", UserService.class);
        FastaDbService fastaDbService = applicationContext.getBean("fastaDbService", FastaDbService.class);

        //set admin user in authentication bean
        User adminUser = userService.findByName("admin");
        userService.fetchAuthenticationRelations(adminUser);
        userBean.setCurrentUser(adminUser);

//        String maxquantPath = "C:/Users/demet/Documents/6453";
//        String parameterPath =  maxquantPath + File.separator + MaxQuantConstants.PARAMETER_FILE.value();
//        String combinedDirectory = maxquantPath + File.separator + MaxQuantConstants.COMBINED_DIRECTORY.value();
//        String txtDirectory = combinedDirectory + File.separator + MaxQuantConstants.TXT_DIRECTORY.value();
//        FastaDb testFastaDb = new FastaDb();
//        testFastaDb.setName("test fasta");
//        testFastaDb.setFileName("SP_human");
//        testFastaDb.setFilePath(txtDirectory + File.separator +  "SP_human.fasta");
//        testFastaDb.setHeaderParseRule("&gt;.*\\|(.*)\\|");
//        fastaDbService.persist(testFastaDb);
//
//        FastaDb contFastaDb = new FastaDb();
//        contFastaDb.setName("cont fasta");
//        contFastaDb.setFileName("contaminants");
//        contFastaDb.setFilePath(txtDirectory + File.separator +  "contaminants.fasta");
//        contFastaDb.setHeaderParseRule("&gt;.*\\|(.*)\\|");
//        fastaDbService.persist(contFastaDb);
//
//        EnumMap<FastaDbType, List<Long>> fastaDbIds = new EnumMap<>(FastaDbType.class);
//        fastaDbIds.put(FastaDbType.PRIMARY, new ArrayList<>(Arrays.asList(testFastaDb.getId())));
//        fastaDbIds.put(FastaDbType.CONTAMINANTS, new ArrayList<>(Arrays.asList(contFastaDb.getId())));
//
//        // to parse everything
//        MaxQuantImport maxQuantImport = new MaxQuantImport(Paths.get(parameterPath),Paths.get(combinedDirectory),Paths.get(combinedDirectory), fastaDbIds, false, true, new ArrayList<>(), "TMT");
//        //@todo fix the nulls
//        MappedData mappedData = maxQuantMapper.mapData(maxQuantImport, null, null);
//        List<AnalyticalRun> analyticalRuns = mappedData.getAnalyticalRuns();
//
//        String msmsFileDirectory = txtDirectory + File.separator + MaxQuantConstants.MSMS_FILE.value();
//        String andromedaDirectory = combinedDirectory + File.separator + MaxQuantConstants.ANDROMEDA_DIRECTORY.value();
//
//        List<String> msmsIDs = new ArrayList<>();
//    //    msmsIDs.add("0");
//    //    msmsIDs.add("1");
//        msmsIDs.add("2");
//        msmsIDs.add("3");
//   //     annotatedSpectraParser.parseSpectra(Paths.get(msmsFileDirectory), Paths.get(andromedaDirectory), msmsIDs);
//
//
//
//        System.out.println("Everything is parsed!");

        EnumMap<FastaDbType, List<Long>> fastaDbs = new EnumMap<>(FastaDbType.class);
        List<Long> pIds = new ArrayList<>();
        pIds.add(1L);
        fastaDbs.put(FastaDbType.PRIMARY, pIds);
        List<Long> cIds = new ArrayList<>();
        pIds.add(2L);
        fastaDbs.put(FastaDbType.CONTAMINANTS, cIds);
        MaxQuantImport maxQuantImport = new MaxQuantImport(
                Paths.get("/home/niels/Desktop/experiments/maxquant_SILAC_integration/mqpar.xml"),
                Paths.get("maxquant_SILAC_integration/combined"),
                Paths.get("/home/niels/Desktop/experiments/maxquant_SILAC_integration/combined"),
                fastaDbs,
                false,
                false,
                new ArrayList<>(),
                "SILAC");
        maxQuantMapper.mapData(maxQuantImport, Paths.get("/home/niels/Desktop/experiments"), Paths.get("/home/niels/Desktop/fastas"));


    }

}
