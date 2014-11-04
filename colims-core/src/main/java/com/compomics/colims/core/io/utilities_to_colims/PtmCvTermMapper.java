package com.compomics.colims.core.io.utilities_to_colims;

import java.util.HashMap;
import java.util.Map;

import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.pride.CvTerm;
import com.compomics.util.pride.PrideObjectsFactory;
import com.compomics.util.pride.PtmToPrideMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * This class maps a Utilities PTM name to a PRIDE CV term instance.
 *
 * @author Niels Hulstaert
 */
@Component("ptmCvTermMapper")
public class PtmCvTermMapper {

    /**
     * The map that holds a the mapping between the PTM name and a CV term PTM.
     */
    private Map<String, CvTerm> ptmToCvTerms = new HashMap<>();
    /**
     * The utilities PtmToPrideMap that holds mappings between utilities PTM.
     * names and CV term PTMs.
     */
    private PtmToPrideMap ptmToPrideMap;

    /**
     * The map of new modifications (key: modification name, value: the
     * modification).
     */
//    private Map<String, Modification> newModifications = new HashMap<>();
    /**
     * No-arg constructor.
     *
     * @throws IOException thrown in case of a I/O related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a
     * class by it's string name.
     */
    public PtmCvTermMapper() throws IOException, ClassNotFoundException {
        ptmToPrideMap = PrideObjectsFactory.getInstance().getPtmToPrideMap();
    }

    public PtmToPrideMap getPtmToPrideMap() {
        return ptmToPrideMap;
    }

    public void setPtmToPrideMap(PtmToPrideMap ptmToPrideMap) throws ClassNotFoundException, FileNotFoundException, IOException {
        PrideObjectsFactory.getInstance().setPtmToPrideMap(ptmToPrideMap);
        this.ptmToPrideMap = ptmToPrideMap;
    }

    /**
     * Initialize the PTM to CV term map.
     *
     * @throws IOException thrown in case of a I/O related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a
     * class by it's string name.
     */
    public void init() throws IOException, ClassNotFoundException {
        //load the PtmToPrideMap from het PrideObjectsFactory
        ptmToPrideMap = PrideObjectsFactory.getInstance().getPtmToPrideMap();
    }

    /**
     * Update the PtmToPrideMap with the PTMs found in the PeptideShaker
     * SearchParameters.
     *
     * @param searchParameters the PeptideShaker SearchParameters
     * @throws IOException thrown in case of a I/O related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a
     * class by it's string name.
     */
    public void updatePtmToPrideMap(final SearchParameters searchParameters) throws IOException, ClassNotFoundException {
        ptmToPrideMap = PtmToPrideMap.loadPtmToPrideMap(searchParameters);
    }

    /**
     * Get the CV term by the PTM name. Returns null if no match was found.
     *
     * @param ptmName the PTM name
     * @return the mapped CV term
     */
    public CvTerm getCvTerm(final String ptmName) {
        CvTerm cvTerm;

        //check the PtmToPrideMap
        cvTerm = ptmToPrideMap.getCVTerm(ptmName);

        if (cvTerm == null) {
            //look in the default CV terms
            cvTerm = PtmToPrideMap.getDefaultCVTerm(ptmName);
        }

        if (cvTerm == null) {
            //look in ptmToCvTerms map
            cvTerm = ptmToCvTerms.get(ptmName);
        }

        return cvTerm;
    }

    /**
     * Add a PTM name to PTM CV term mapping to the map.
     *
     * @param ptmName the PTM name
     * @param cvTerm the PTM CV term
     */
    public void addCvTerm(final String ptmName, final CvTerm cvTerm) {
        if (!ptmToCvTerms.containsKey(ptmName)) {
            ptmToCvTerms.put(ptmName, cvTerm);
        }
    }

}
