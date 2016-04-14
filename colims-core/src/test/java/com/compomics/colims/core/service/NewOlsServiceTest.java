package com.compomics.colims.core.service;

import com.compomics.colims.core.model.ols.Ontology;
import com.compomics.colims.core.model.ols.OlsSearchResult;
import com.compomics.colims.core.model.ols.SearchResultMetadata;
import com.compomics.colims.model.Modification;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Rollback
@Transactional
public class NewOlsServiceTest {

    @Autowired
    private OlsService newOlsService;

    /**
     * Clear the modifications cache.
     */
    @After
    public void clearCache() {
        newOlsService.getModificationsCache().clear();
    }

    @Test
    public void testGetAllOntologies() throws HttpClientErrorException, IOException {
        List<Ontology> allOntologies = newOlsService.getAllOntologies();

        Assert.assertFalse(allOntologies.isEmpty());
        Assert.assertEquals(147, allOntologies.size());
    }

    @Test
    public void testGetOntologiesByNamespace() throws HttpClientErrorException, IOException {
        List<String> namespaces = new ArrayList<>();
        namespaces.add("ms");
        //add one nonsense namespace
        namespaces.add("nonsense");
        List<Ontology> foundOntologies = newOlsService.getOntologiesByNamespace(namespaces);

        Assert.assertFalse(foundOntologies.isEmpty());
        //only one ontology is expected to be found
        Assert.assertEquals(1, foundOntologies.size());
    }

    @Test
    public void testDoPagedSearch() throws HttpClientErrorException, IOException {
        String query = "MS/MS in Time";

        /**
         * Test with one ontology namespace and one search field.
         */
        List<String> namespaces = new ArrayList<>();
        namespaces.add("ms");

        //first fetch the metadata
        SearchResultMetadata pagedSearchMetadata = newOlsService.getPagedSearchMetadata(query, namespaces, EnumSet.of(OlsSearchResult.SearchField.LABEL));
        int numberOfResultPages = pagedSearchMetadata.getNumberOfResultPages();

        Assert.assertFalse(pagedSearchMetadata.getRequestUrl().isEmpty());

        //then fetch the first page of the results
        List<OlsSearchResult> searchResults = newOlsService.pagedSearch(pagedSearchMetadata.getRequestUrl(), 0, 10);

        Assert.assertFalse(searchResults.isEmpty());
        Assert.assertTrue(searchResults.size() == 10);

        //only label fields should be returned
        Assert.assertTrue(searchResults.stream().allMatch(s -> s.getMatchedFields().containsKey(OlsSearchResult.SearchField.LABEL)));

        //fetch the last page
        searchResults = newOlsService.pagedSearch(pagedSearchMetadata.getRequestUrl(), numberOfResultPages - 1, 10);

        Assert.assertFalse(searchResults.isEmpty());

        /**
         * Test with all ontologies and 2 search fields.
         */
        pagedSearchMetadata = newOlsService.getPagedSearchMetadata(query, namespaces, EnumSet.of(OlsSearchResult.SearchField.LABEL, OlsSearchResult.SearchField.SYNONYM));

        Assert.assertFalse(pagedSearchMetadata.getRequestUrl().isEmpty());

        //then fetch the first page of the results
        searchResults = newOlsService.pagedSearch(pagedSearchMetadata.getRequestUrl(), 0, 10);

        Assert.assertFalse(searchResults.isEmpty());
        Assert.assertTrue(searchResults.size() == 10);

        //only label and synonym fields should be returned
        Assert.assertTrue(searchResults.stream().allMatch(s -> s.getMatchedFields().containsKey(OlsSearchResult.SearchField.LABEL) || s.getMatchedFields().containsKey(OlsSearchResult.SearchField.SYNONYM)));

        /**
         * Test with all ontologies and the default search fields.
         */
        pagedSearchMetadata = newOlsService.getPagedSearchMetadata(query, namespaces, EnumSet.of(OlsSearchResult.SearchField.LABEL, OlsSearchResult.SearchField.SYNONYM));

        Assert.assertFalse(pagedSearchMetadata.getRequestUrl().isEmpty());

        //then fetch the first page of the results
        searchResults = newOlsService.pagedSearch(pagedSearchMetadata.getRequestUrl(), 0, 10);

        Assert.assertFalse(searchResults.isEmpty());
        Assert.assertTrue(searchResults.size() == 10);
    }

//    /**
//     * Test the find modification by accession method from the OlsService.
//     */
//    @Test
//    public void testFindModificationByAccession() {
////        //try to find a non existing modification
////        Modification modification = newOlsService.findModificationByAccession(Modification.class, "MOD:00935999");
////
////        Assert.assertNull(modification);
//
//        //try to find an existing modification
//        Modification modification = newOlsService.findModificationByAccession(Modification.class, "MOD:00935");
//
//        Assert.assertNotNull(modification);
//        Assert.assertEquals("MOD:00935", modification.getAccession());
//        Assert.assertEquals("methionine oxidation with neutral loss of 64 Da", modification.getName());
//        Assert.assertEquals(-63.998286, modification.getMonoIsotopicMassShift(), 0.001);
//        Assert.assertEquals(-64.1, modification.getAverageMassShift(), 0.001);
//    }
//

    /**
     * Test the find modification by exact name method from the OlsService.
     */
    @Test
    public void testFindModificationByExactName() throws IOException {
        //try to find a non existing modification
//        Modification modification = newOlsService.findModificationByExactName(Modification.class, "non existing modification");

//        Assert.assertNull(modification);

        //try to find an existing modification
        Modification modification = newOlsService.findModificationByExactName(Modification.class, "methionine oxidation with neutral loss of 64 Da");

        Assert.assertNotNull(modification);
        Assert.assertEquals("MOD:00935", modification.getAccession());
        Assert.assertEquals("methionine oxidation with neutral loss of 64 Da", modification.getName());
        Assert.assertEquals(-63.998286, modification.getMonoIsotopicMassShift(), 0.001);
        Assert.assertEquals(-64.1, modification.getAverageMassShift(), 0.001);
    }
//
//    /**
//     * Test the find modification by name method from the OlsService.
//     */
//    @Test
//    public void testFindModificationByName() {
//        //try to find a non existing modification
//        List<Modification> modifications = olsService.findModificationByName("non existing modification");
//        Assert.assertTrue(modifications.isEmpty());
//
//        //try to find an existing modification, the ols web service should return 3 mods
//        modifications = olsService.findModificationByName("oxidation of m");
//        Assert.assertEquals(3, modifications.size());
//    }
//
//    /**
//     * Test the find modification by name method from the OlsService.
//     */
//    @Test
//    public void testNewFindModificationByName() {
////        //try to find a non existing modification
////        List<Modification> modifications = newOlsService.findModificationByName("non existing modification");
////        Assert.assertTrue(modifications.isEmpty());
//
//        //try to find an existing modification, the ols web service should return 3 mods
//        List<Modification> modifications = newOlsService.findModificationByName("oxidation of m");
//        Assert.assertEquals(3, modifications.size());
//    }
//
//    /**
//     * Test the find a modification by name and UNIMOD accession method from the OlsService.
//     */
//    @Test
//    public void testFindModificationByNameAndUnimodAccession_1() {
//        Modification modification = olsService.findModificationByNameAndUnimodAccession(Modification.class, "Phospho", "UNIMOD:21");
//        Assert.assertNotNull(modification);
//        Assert.assertNotNull(modification.getAccession());
//    }
//
//    /**
//     * Test the modifications cache from the OlsService.
//     */
//    @Test
//    public void testModificationsCache() {
//        int cacheSize = olsService.getModificationsCache().size();
//        Assert.assertFalse(olsService.getModificationsCache().containsKey("UNIMOD:385"));
//
//        //first, try to find a SearchModification instance
//        SearchModification searchModification = olsService.findModificationByNameAndUnimodAccession(SearchModification.class, "Ammonia-loss", "UNIMOD:385");
//        Assert.assertNotNull(searchModification);
//        Assert.assertNotNull(searchModification.getAccession());
//
//        //the modification should have been added twice to the cache,
//        //one time with the PSI-MOD accession as key and one time with the UNIMOD accession as key.
//        Assert.assertEquals(cacheSize + 2, olsService.getModificationsCache().size());
//
//        Modification modification = olsService.findModificationByNameAndUnimodAccession(Modification.class, "Ammonia-loss", "UNIMOD:385");
//        Assert.assertNotNull(modification);
//        Assert.assertNotNull(modification.getAccession());
//
//        //the modification should not have been added to the cache
//        Assert.assertEquals(cacheSize + 2, olsService.getModificationsCache().size());
//    }
//
//    /**
//     * Test the find enzyme CV param by name method from the OlsService.
//     */
//    @Test
//    public void testFindEnzymeByName() {
//        //try to find a non existing enzyme
//        TypedCvParam enzyme = olsService.findEnzymeByName("Non existing enzyme");
//
//        Assert.assertNull(enzyme);
//
//        //try to find an existing enzyme (lower case)
//        enzyme = olsService.findEnzymeByName("trypsin");
//
//        Assert.assertNotNull(enzyme);
//        Assert.assertEquals("MS:1001251", enzyme.getAccession());
//        Assert.assertEquals("Trypsin", enzyme.getName());
//
//        //try to find an existing enzyme (upper case)
//        enzyme = olsService.findEnzymeByName("TRYPSIN");
//
//        Assert.assertNotNull(enzyme);
//        Assert.assertEquals("MS:1001251", enzyme.getAccession());
//        Assert.assertEquals("Trypsin", enzyme.getName());
//    }
}
