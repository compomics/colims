package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.enums.FastaDbType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.AssertionErrors;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantProteinGroupsParserTest {

    private Path proteinGroupsFile;

    private LinkedHashMap<FastaDb, Path> fastaDbs = new LinkedHashMap<>();
    private EnumMap<FastaDbType, List<FastaDb>> fastaDbEnumMap = new EnumMap<>(FastaDbType.class);
    @Autowired
    private MaxQuantProteinGroupsParser maxQuantProteinGroupsParser;
    @Autowired
    private MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;

    public MaxQuantProteinGroupsParserTest() throws IOException {
        //proteinGroupsFile = new ClassPathResource("data/maxquant/proteinGroups_subset.txt").getFile().toPath();
    }

    /**
     * SILAC quantification parsing test.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testSilac() throws Exception {
        Path proteinGroupsFile = new ClassPathResource("data/maxquant/SILAC/combined/txt/proteinGroups.txt").getFile().toPath();
        Path combinedDirectory = new ClassPathResource("data/maxquant/SILAC/combined").getFile().toPath();
        Path mqparFile = new ClassPathResource("data/maxquant/SILAC/mqpar.xml").getFile().toPath();

        fastaDbs.put(MaxQuantTestSuite.spHuman_01_2017_FastaDb, MaxQuantTestSuite.spHuman_01_2017_FastaDbPath);
        fastaDbs.put(MaxQuantTestSuite.lloFastaDb, MaxQuantTestSuite.lloFastaDbPath);
        fastaDbs.put(MaxQuantTestSuite.contaminantsFastaDb, MaxQuantTestSuite.contaminantsFastaDbPath);

        fastaDbEnumMap.put(FastaDbType.PRIMARY, Arrays.asList(MaxQuantTestSuite.oryzaFastaDb));
        fastaDbEnumMap.put(FastaDbType.ADDITIONAL, Arrays.asList(MaxQuantTestSuite.lloFastaDb));
        fastaDbEnumMap.put(FastaDbType.CONTAMINANTS, Arrays.asList(MaxQuantTestSuite.contaminantsFastaDb));

        maxQuantSearchSettingsParser.clear();
        maxQuantSearchSettingsParser.parse(combinedDirectory, mqparFile, fastaDbEnumMap);

        maxQuantProteinGroupsParser.clear();
        maxQuantProteinGroupsParser.parse(proteinGroupsFile,
                fastaDbs, MaxQuantImport.SILAC, true, new ArrayList<>());

        Map<Integer, ProteinGroup> result = maxQuantProteinGroupsParser.getProteinGroups();

        Assert.assertEquals(383, result.size());
        ProteinGroup proteinGroup = result.get(0);
        Assert.assertEquals(24, proteinGroup.getProteinGroupQuants().size());
    }

    /**
     * ICAT quantification parsing test.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testIcat() throws Exception {
        Path proteinGroupsFile = new ClassPathResource("data/maxquant/ICAT/combined/txt/proteinGroups.txt").getFile().toPath();
        Path combinedDirectory = new ClassPathResource("data/maxquant/ICAT/combined").getFile().toPath();
        Path mqparFile = new ClassPathResource("data/maxquant/ICAT/mqpar.xml").getFile().toPath();

        fastaDbs.put(MaxQuantTestSuite.spHuman_01_2017_FastaDb, MaxQuantTestSuite.spHuman_01_2017_FastaDbPath);
        fastaDbs.put(MaxQuantTestSuite.contaminantsFastaDb, MaxQuantTestSuite.contaminantsFastaDbPath);

        fastaDbEnumMap.put(FastaDbType.PRIMARY, Arrays.asList(MaxQuantTestSuite.oryzaFastaDb));
        fastaDbEnumMap.put(FastaDbType.CONTAMINANTS, Arrays.asList(MaxQuantTestSuite.contaminantsFastaDb));

        maxQuantSearchSettingsParser.clear();
        maxQuantSearchSettingsParser.parse(combinedDirectory, mqparFile, fastaDbEnumMap);

        maxQuantProteinGroupsParser.clear();
        maxQuantProteinGroupsParser.parse(proteinGroupsFile,
                fastaDbs, MaxQuantImport.ICAT, true, new ArrayList<>());

        Map<Integer, ProteinGroup> result = maxQuantProteinGroupsParser.getProteinGroups();

        Assert.assertEquals(89, result.size());
        ProteinGroup proteinGroup = result.get(0);
        Assert.assertEquals(9, proteinGroup.getProteinGroupQuants().size());
    }

    /**
     * TMT quantification parsing test.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testTmt() throws Exception {
        Path proteinGroupsFile = new ClassPathResource("data/maxquant/TMT/combined/txt/proteinGroups.txt").getFile().toPath();
        Path combinedDirectory = new ClassPathResource("data/maxquant/TMT/combined").getFile().toPath();
        Path mqparFile = new ClassPathResource("data/maxquant/TMT/mqpar.xml").getFile().toPath();

        fastaDbs.put(MaxQuantTestSuite.spHuman_01_2017_FastaDb, MaxQuantTestSuite.spHuman_01_2017_FastaDbPath);
        fastaDbs.put(MaxQuantTestSuite.contaminantsFastaDb, MaxQuantTestSuite.contaminantsFastaDbPath);
        fastaDbEnumMap.put(FastaDbType.PRIMARY, Arrays.asList(MaxQuantTestSuite.spHumanFastaDb));
        fastaDbEnumMap.put(FastaDbType.CONTAMINANTS, Arrays.asList(MaxQuantTestSuite.contaminantsFastaDb));

        maxQuantSearchSettingsParser.clear();
        maxQuantSearchSettingsParser.parse(combinedDirectory, mqparFile, fastaDbEnumMap);

        maxQuantProteinGroupsParser.clear();
        List<String> optionalHeaders = new ArrayList<>();
        optionalHeaders.add("ibaq");
        maxQuantProteinGroupsParser.parse(proteinGroupsFile,
                fastaDbs, MaxQuantImport.TMT, true, optionalHeaders);

        Map<Integer, ProteinGroup> result = maxQuantProteinGroupsParser.getProteinGroups();

        Assert.assertEquals(273, result.size());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Double> intensities = objectMapper.readValue(result.get(0).getProteinGroupQuants().get(0).getLabels(), new TypeReference<HashMap<String, Double>>() {
        });
        Assert.assertEquals(11, intensities.size());
        //look for the optional ibaq intensity
        Optional<String> foundLabel = intensities.keySet().stream().filter(label -> label.startsWith("ibaq")).findFirst();
        Assert.assertTrue(foundLabel.isPresent());
    }

    /**
     * Test of parse method of class MaxQuantProteinGroupParser.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testParse() throws Exception {
        fastaDbs.put(MaxQuantTestSuite.spHumanFastaDb, MaxQuantTestSuite.spHumanFastaDbPath);
        fastaDbs.put(MaxQuantTestSuite.contaminantsFastaDb, MaxQuantTestSuite.contaminantsFastaDbPath);
        fastaDbEnumMap.put(FastaDbType.PRIMARY, Arrays.asList(MaxQuantTestSuite.spHumanFastaDb));
        fastaDbEnumMap.put(FastaDbType.CONTAMINANTS, Arrays.asList(MaxQuantTestSuite.contaminantsFastaDb));

        maxQuantProteinGroupsParser.clear();
        maxQuantProteinGroupsParser.parse(MaxQuantTestSuite.proteinGroupsFile,
                fastaDbs, MaxQuantImport.SILAC, true, new ArrayList<>());

        Map<Integer, ProteinGroup> result = maxQuantProteinGroupsParser.getProteinGroups();

        //number of entries in the proteinGroups.txt file - number of reverse proteins
        Assert.assertEquals(672, result.size());
    }

    /**
     * Test of parse method of class MaxQuantProteinGroupParser.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testParseWithoutContaminants() throws Exception {
        fastaDbs.put(MaxQuantTestSuite.spHumanFastaDb, MaxQuantTestSuite.spHumanFastaDbPath);
        fastaDbs.put(MaxQuantTestSuite.contaminantsFastaDb, MaxQuantTestSuite.contaminantsFastaDbPath);
        fastaDbEnumMap.put(FastaDbType.PRIMARY, Arrays.asList(MaxQuantTestSuite.spHumanFastaDb));
        fastaDbEnumMap.put(FastaDbType.CONTAMINANTS, Arrays.asList(MaxQuantTestSuite.contaminantsFastaDb));

        maxQuantProteinGroupsParser.clear();
        maxQuantProteinGroupsParser.parse(MaxQuantTestSuite.proteinGroupsFile,
                fastaDbs, MaxQuantImport.SILAC, false, new ArrayList<>());

        Map<Integer, ProteinGroup> result = maxQuantProteinGroupsParser.getProteinGroups();

        //number of entries in the proteinGroups.txt file - number of reverse proteins
        Assert.assertEquals(661, result.size());
    }
}