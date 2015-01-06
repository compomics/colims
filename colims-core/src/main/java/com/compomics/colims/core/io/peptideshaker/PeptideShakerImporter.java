package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.DataImporter;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.SearchSettingsMapper;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.enums.SearchEngineType;
import eu.isas.peptideshaker.utils.CpsParent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The DataImporter class for PeptideShaker projects.
 *
 * @author Niels Hulstaert
 */
@Component("peptideShakerImporter")
public class PeptideShakerImporter implements DataImporter {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(PeptideShakerImporter.class);

    /**
     * The unarchived PeptideShaker import.
     */
    private UnpackedPeptideShakerImport unpackedPeptideShakerImport;
    /**
     * The utilities to Colims search settings mapper.
     */
    @Autowired
    private SearchSettingsMapper searchSettingsMapper;
    /**
     * Maps the PeptideShaker search input and results to Colims classes.
     */
    @Autowired
    private PSInputAndResultsMapper inputAndResultsMapper;

    @Override
    public void initImport(final DataImport dataImport) {
        if (dataImport instanceof UnpackedPeptideShakerImport) {
            unpackedPeptideShakerImport = (UnpackedPeptideShakerImport) dataImport;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void clear() {
        try {
            inputAndResultsMapper.clear();
        } catch (IOException | SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public SearchAndValidationSettings importSearchSettings() throws MappingException {
        SearchAndValidationSettings searchAndValidationSettings;

        try {
            CpsParent cpsParent = unpackedPeptideShakerImport.getCpsParent();
            String version = cpsParent.getProjectDetails().getPeptideShakerVersion();

            List<File> identificationFiles = new ArrayList<>();
            identificationFiles.add(unpackedPeptideShakerImport.getPeptideShakerCpsArchive());
            searchAndValidationSettings = searchSettingsMapper.map(SearchEngineType.PEPTIDESHAKER, version, unpackedPeptideShakerImport.getFastaDb(), cpsParent.getIdentificationParameters().getSearchParameters(), identificationFiles, false);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex.getMessage(), ex);
        }

        return searchAndValidationSettings;
    }

    @Override
    public QuantificationSettings importQuantSettings() throws MappingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AnalyticalRun> importInputAndResults(final SearchAndValidationSettings searchAndValidationSettings, final QuantificationSettings quantificationSettings) throws MappingException {
        List<AnalyticalRun> runs;

        try {
            runs = inputAndResultsMapper.map(searchAndValidationSettings, unpackedPeptideShakerImport);
        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException | IllegalArgumentException | MzMLUnmarshallerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex.getMessage(), ex);
        }

        return runs;
    }

}
