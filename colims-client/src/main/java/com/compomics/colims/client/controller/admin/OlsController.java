package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.OlsErrorMessageEvent;
import com.compomics.colims.client.model.table.model.OntologySearchResultTableModel;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.OlsDialog;
import com.compomics.colims.core.model.ols.Ontology;
import com.compomics.colims.core.model.ols.OntologyTitleComparator;
import com.compomics.colims.core.model.ols.SearchResult;
import com.compomics.colims.core.service.OlsService;
import com.google.common.eventbus.EventBus;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author Niels Hulstaert
 */
@Component("olsController")
@Lazy
public class OlsController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(OlsController.class);

    private static final String OLS_BASE = "http://www.ebi.ac.uk/ols/beta/ontologies/%s/terms?iri=%s";

    //model
    /**
     * The default ontology namespaces.
     */
    @Value("#{'${ontology.default_namespaces}'.split(',')}")
    private List<String> defaultOntologyNamespaces = new ArrayList<>();
    private List<Ontology> ontologies = new ArrayList<>();
    private OntologySearchResultTableModel ontologySearchResultTableModel;
    private SearchResultTableMouseAdapter searchResultTableMouseAdapter = new SearchResultTableMouseAdapter();
    //view
    private OlsDialog olsDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    private EventBus eventBus;
    //services
    @Autowired
    private OlsService newOlsService;

    @Override
    @PostConstruct
    public void init() {
        //init view
        olsDialog = new OlsDialog(mainController.getMainFrame(), true);

        if (defaultOntologyNamespaces.isEmpty()) {
            //add the "search all ontologies" stub ontology
            ontologies.add(Ontology.ALL_ONTOLOGIES);
        } else {
            try {
                ontologies.addAll(newOlsService.getOntologiesByNamespace(defaultOntologyNamespaces));
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
                eventBus.post(new OlsErrorMessageEvent(OlsErrorMessageEvent.OlsError.PARSE_ERROR));
            } catch (HttpClientErrorException ex) {
                LOGGER.error(ex.getMessage(), ex);
                eventBus.post(new OlsErrorMessageEvent(OlsErrorMessageEvent.OlsError.CONNECTION_ERROR));
            }
        }

        //init the dual list
        olsDialog.getOntologiesDualList().init(new OntologyTitleComparator());
        olsDialog.getOntologiesDualList().populateLists(new ArrayList(), ontologies);

        //init the search results table
        ontologySearchResultTableModel = new OntologySearchResultTableModel();
        olsDialog.getSearchResultTable().setModel(ontologySearchResultTableModel);

        //set the default radio button values
        if (defaultOntologyNamespaces.isEmpty()) {
            olsDialog.getAllOntologiesRadioButton().setSelected(true);
            olsDialog.getOntologiesDualList().setEnabled(false);
        } else {
            olsDialog.getPreselectedOntologiesRadioButton().setSelected(true);
        }

        olsDialog.getDefaultFieldsRadioButton().setSelected(true);
        enableSearchFieldCheckBoxes(false);

        //set column widths
        olsDialog.getSearchResultTable().getColumnModel().getColumn(OntologySearchResultTableModel.ONTOLOGY_NAMESPACE).setPreferredWidth(100);
        olsDialog.getSearchResultTable().getColumnModel().getColumn(OntologySearchResultTableModel.TERM_ACCESSION).setPreferredWidth(200);
        olsDialog.getSearchResultTable().getColumnModel().getColumn(OntologySearchResultTableModel.MATCH_FIELD).setPreferredWidth(150);
        olsDialog.getSearchResultTable().getColumnModel().getColumn(OntologySearchResultTableModel.MATCH_HIGHLIGHT).setPreferredWidth(500);

        //add listeners
        olsDialog.getSearchResultTable().addMouseListener(searchResultTableMouseAdapter);
        olsDialog.getSearchResultTable().addMouseMotionListener(searchResultTableMouseAdapter);

        olsDialog.getSearchResultTable().getSelectionModel().addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                int selectedRow = olsDialog.getSearchResultTable().getSelectedRow();
                if (selectedRow != -1 && !ontologySearchResultTableModel.getSearchResults().isEmpty()) {
                    SearchResult selectedSearchResult = ontologySearchResultTableModel.getSearchResults().get(selectedRow);
                    System.out.println("------------------------");
                } else {
//                    clear
                }
            }
        });

        olsDialog.getSearchButton().addActionListener(e -> {
            //basic validation
            String searchInput = olsDialog.getSearchInputTextField().getText();
            if (searchInput.length() >= 2 && searchInput.length() < 30) {
                EnumSet<SearchResult.SearchField> searchFields;
                //get the selected search fields when not using the default ones
                if (!olsDialog.getDefaultFieldsRadioButton().isSelected()) {
                    searchFields = getSearchFields();
                } else {
                    searchFields = EnumSet.noneOf(SearchResult.SearchField.class);
                }
                try {
                    List<SearchResult> searchResults = newOlsService.search(searchInput, ontologies.stream().map(o -> o.getNameSpace()).collect(Collectors.toList()), searchFields);
                    ontologySearchResultTableModel.setSearchResults(searchResults);
                } catch (HttpClientErrorException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    eventBus.post(new OlsErrorMessageEvent(OlsErrorMessageEvent.OlsError.CONNECTION_ERROR));
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    eventBus.post(new OlsErrorMessageEvent(OlsErrorMessageEvent.OlsError.PARSE_ERROR));
                }
            } else {
                eventBus.post(new MessageEvent("Search input", "Please provide a valid search term.", JOptionPane.WARNING_MESSAGE));
            }
        });

        olsDialog.getGetAllOntologiesButton().addActionListener(e -> {
            try {
                List<Ontology> allOntologies = newOlsService.getAllOntologies();
                olsDialog.getOntologiesDualList().populateLists(allOntologies, olsDialog.getOntologiesDualList().getAddedItems());
            } catch (HttpClientErrorException ex) {
                LOGGER.error(ex.getMessage(), ex);
                eventBus.post(new OlsErrorMessageEvent(OlsErrorMessageEvent.OlsError.CONNECTION_ERROR));
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
                eventBus.post(new OlsErrorMessageEvent(OlsErrorMessageEvent.OlsError.PARSE_ERROR));
            }
        });

        olsDialog.getOntologiesDualList().addPropertyChangeListener(DualList.CHANGED, evt -> {
            ontologies.clear();
            ontologies.addAll(olsDialog.getOntologiesDualList().getAddedItems());
        });

        olsDialog.getAllOntologiesRadioButton().addActionListener(e -> {
            if (olsDialog.getAllOntologiesRadioButton().isSelected()) {
                olsDialog.getOntologiesDualList().setEnabled(false);
            }
        });

        olsDialog.getPreselectedOntologiesRadioButton().addActionListener(e -> {
            if (olsDialog.getPreselectedOntologiesRadioButton().isSelected()) {
                olsDialog.getOntologiesDualList().setEnabled(true);
            }
        });

        olsDialog.getDefaultFieldsRadioButton().addActionListener(e -> {
            if (olsDialog.getDefaultFieldsRadioButton().isSelected()) {
                enableSearchFieldCheckBoxes(false);
            }
        });

        olsDialog.getCustomFieldsRadioButton().addActionListener(e -> {
            if (olsDialog.getCustomFieldsRadioButton().isSelected()) {
                enableSearchFieldCheckBoxes(true);
            }
        });

        olsDialog.getCancelButton().addActionListener(e -> {
            olsDialog.dispose();
        });

    }

    @Override
    public void showView() {
        //clear search results
        ontologySearchResultTableModel.setSearchResults(new ArrayList<>());

        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), olsDialog);
        olsDialog.setVisible(true);
    }

    /**
     * Enable or disable the search field checkboxes.
     *
     * @param enable whether to enable or disable the checkboxes
     */
    private void enableSearchFieldCheckBoxes(boolean enable) {
        olsDialog.getLabelsCheckBox().setEnabled(enable);
        olsDialog.getSynonymsCheckBox().setEnabled(enable);
        olsDialog.getDescriptionsCheckBox().setEnabled(enable);
        olsDialog.getIdentifiersCheckBox().setEnabled(enable);
        olsDialog.getAnnotationPropertiesCheckBox().setEnabled(enable);
    }

    /**
     * Get the selected search fields from the search field checkboxes.
     *
     * @return the set of selected search fields
     */
    private EnumSet<SearchResult.SearchField> getSearchFields() {
        EnumSet<SearchResult.SearchField> searchFields = EnumSet.noneOf(SearchResult.SearchField.class);

        if (olsDialog.getLabelsCheckBox().isSelected()) {
            searchFields.add(SearchResult.SearchField.LABEL);
        }
        if (olsDialog.getSynonymsCheckBox().isSelected()) {
            searchFields.add(SearchResult.SearchField.SYNONYM);
        }
        if (olsDialog.getDescriptionsCheckBox().isSelected()) {
            searchFields.add(SearchResult.SearchField.DESCRIPTION);
        }
        if (olsDialog.getIdentifiersCheckBox().isSelected()) {
            searchFields.add(SearchResult.SearchField.IDENTIFIER);
        }
        if (olsDialog.getAnnotationPropertiesCheckBox().isSelected()) {
            searchFields.add(SearchResult.SearchField.ANNOTATION_PROPERTY);
        }

        return searchFields;
    }

    class SearchResultTableMouseAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            int columnIndex = olsDialog.getSearchResultTable().columnAtPoint(new Point(e.getX(), e.getY()));
            if (columnIndex == OntologySearchResultTableModel.TERM_ACCESSION) {
                int rowIndex = olsDialog.getSearchResultTable().rowAtPoint(new Point(e.getX(), e.getY()));
                SearchResult selectedSearchResult = ontologySearchResultTableModel.getSearchResults().get(rowIndex);
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(String.format(OLS_BASE, selectedSearchResult.getOntologyNamespace(), URLEncoder.encode(selectedSearchResult.getIri(), "UTF-8"))));
                    } catch (IOException | URISyntaxException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }
            }
        }

//            @Override
//            public void mouseEntered(MouseEvent e) {
//                int columnIndex = olsDialog.getSearchResultTable().columnAtPoint(new Point(e.getX(), e.getY()));
//                if (columnIndex == OntologySearchResultTableModel.TERM_ACCESSION) {
//                    olsDialog.getSearchResultTable().setCursor(new Cursor(Cursor.HAND_CURSOR));
//                }
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                int columnIndex = olsDialog.getSearchResultTable().columnAtPoint(new Point(e.getX(), e.getY()));
//                if (columnIndex != OntologySearchResultTableModel.TERM_ACCESSION) {
//                    olsDialog.getSearchResultTable().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//                }
//            }
        @Override
        public void mouseMoved(MouseEvent e) {
            int columnIndex = olsDialog.getSearchResultTable().columnAtPoint(new Point(e.getX(), e.getY()));
            if (columnIndex != OntologySearchResultTableModel.TERM_ACCESSION) {
                olsDialog.getSearchResultTable().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                olsDialog.getSearchResultTable().setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        }

    }

}
