package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.AnalyticalRunSetupController;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.view.admin.FastaDbManagementDialog;
import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.factory.CvTermFactory;
import java.awt.Window;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.uib.olsdialog.OLSDialog;
import no.uib.olsdialog.OLSInputable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("fastaDbManagementController")
public class FastaDbManagementController implements Controllable, OLSInputable {

    private static final String NEW_TAXONOMY_TERM = "add";
    private static final String UPDATE_TAXONOMY_TERM = "update";

    //view
    private FastaDbManagementDialog fastaDbManagementDialog;
    //parent controller
    @Autowired
    private AnalyticalRunSetupController analyticalRunSetupController;

    /**
     * Show the OLS dialog.
     *
     * @param isNewCvTerm is the CV term new or did already exist in the DB
     */
    private void showOlsDialog(boolean isNewCvTerm) {
        String ontology = "PSI Mass Spectrometry Ontology [MS]";

        Map<String, List<String>> preselectedOntologies = new HashMap<>();
        preselectedOntologies.put("MS", null);

        String field = (isNewCvTerm) ? NEW_TAXONOMY_TERM : UPDATE_TAXONOMY_TERM;

        //show new OLS dialog
        new OLSDialog(fastaDbManagementDialog, this, true, field, ontology, null, preselectedOntologies);
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showView() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insertOLSResult(String field, String selectedValue, String accession, String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, Map<String, String> metadata) {

    }

    @Override
    public Window getWindow() {
        return fastaDbManagementDialog;
    }

}
