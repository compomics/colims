package com.compomics.colims.core.mapper.impl.utilitiesToColims;

import java.util.HashMap;
import java.util.Map;

import com.compomics.colims.model.Modification;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.pride.CvTerm;
import com.compomics.util.pride.PrideObjectsFactory;
import com.compomics.util.pride.PtmToPrideMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("ptmCvTermMapper")
public class PtmCvTermMapper {

    /**
     * The map that holds a the mapping between the PTM name and a CV term PTM
     */
    private Map<String, CvTerm> ptmToCvTerms = new HashMap<>();
    /**
     * The utilities PtmToPrideMap that holds mappings between utilities PTM
     * names and CV term PTMs
     */
    private PtmToPrideMap ptmToPrideMap;
    /**
     * The map of new modifications (key: modification name, value: the
     * modification)
     */
    private Map<String, Modification> newModifications = new HashMap<>();
    /**
     * Compomics utilities spectrum factory
     *
     * @todo think of a good way to import this .cus file from the .compomics
     * folder
     */
    private PTMFactory pTMFactory = PTMFactory.getInstance();

    public PtmCvTermMapper() throws FileNotFoundException, IOException, ClassNotFoundException {
        ptmToPrideMap = PrideObjectsFactory.getInstance().getPtmToPrideMap();
    }

    /**
     * Init the PTM to CV term map
     */
    public void init() throws FileNotFoundException, IOException, ClassNotFoundException {
        //load the PtmToPrideMap from het PrideObjectsFactory
        ptmToPrideMap = PrideObjectsFactory.getInstance().getPtmToPrideMap();
    }

    /**
     * Update the PtmToPrideMap with the PTMs found in the PeptideShaker
     * SearchParameters
     *
     * @param searchParameters the PeptideShaker SearchParameters
     */
    public void updatePtmToPrideMap(SearchParameters searchParameters) throws FileNotFoundException, IOException, ClassNotFoundException {
        ptmToPrideMap = PtmToPrideMap.loadPtmToPrideMap(searchParameters);
    }

    /**
     * Get the CV term by the PTM name
     *
     * @param ptmName the PTM name
     * @return the mapped CV term
     */
    public CvTerm getCvTerm(String ptmName) {
        CvTerm cvTerm;

        //check the PtmToPrideMap
        cvTerm = ptmToPrideMap.getCVTerm(ptmName);

        if (cvTerm == null) {
            //look in ptmToCvTerms map
            cvTerm = ptmToCvTerms.get(ptmName);
        }

        return cvTerm;
    }

    /**
     * Add a PTM name to PTM CV term mapping to the map
     *
     * @param ptmName the PTM name
     * @param cvTerm the PTM CV term
     */
    public void addCvTerm(String ptmName, CvTerm cvTerm) {
        if (!ptmToCvTerms.containsKey(ptmName)) {
            ptmToCvTerms.put(ptmName, cvTerm);
        }
    }
}
