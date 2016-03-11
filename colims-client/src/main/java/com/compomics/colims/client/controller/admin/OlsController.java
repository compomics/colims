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
    @Value("#{'${ontology.preselected_namespaces}'.split(',')}")
    private final List<String> preselectedOntologyNamespaces = new ArrayList<>();
    private final List<Ontology> preselectedOntologies = new ArrayList<>();
    private final OntologySearchResultTableModel ontologySearchResultTableModel = new OntologySearchResultTableModel();
    private final SearchResultTableMouseAdapter searchResultTableMouseAdapter = new SearchResultTableMouseAdapter();
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

        //init the dual list
        olsDialog.getOntologiesDualList().init(new OntologyTitleComparator());

        getOntologies();

        //init the search results table
        olsDialog.getSearchResultTable().setModel(ontologySearchResultTableModel);

        //set the default radio button values
        if (preselectedOntologyNamespaces.isEmpty()) {
            olsDialog.getAllOntologiesRadioButton().setSelected(true);
            olsDialog.getOntologiesDualList().setEnabled(false);
        } else {
            olsDialog.getPreselectedOntologiesRadioButton().setSelected(true);
        }

        //init the search fields settings
        olsDialog.getDefaultFieldsRadioButton().setSelected(true);
        enableSearchFieldCheckBoxes(false);

        //set column widths
        olsDialog.getSearchResultTable().getColumnModel().getColumn(OntologySearchResultTableModel.ONTOLOGY_NAMESPACE).setPreferredWidth(100);
        olsDialog.getSearchResultTable().getColumnModel().getColumn(OntologySearchResultTableModel.TERM_ACCESSION).setPreferredWidth(200);
        olsDialog.getSearchResultTable().getColumnModel().getColumn(OntologySearchResultTableModel.MATCHES).setPreferredWidth(500);

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
            //validate the user input
            List<String> validationMessages = validate();
            if (validationMessages.isEmpty()) {
                try {
                    String searchInput = olsDialog.getSearchInputTextField().getText();
                    List<Ontology> ontologiesToSearch = new ArrayList<>();
                    if (olsDialog.getPreselectedOntologiesRadioButton().isSelected()) {
                        ontologiesToSearch = olsDialog.getOntologiesDualList().getAddedItems();
                    }
                    List<SearchResult> searchResults = newOlsService.search(searchInput, ontologiesToSearch.stream().map(o -> o.getNameSpace()).collect(Collectors.toList()), getSearchFields());
                    ontologySearchResultTableModel.setSearchResults(searchResults);
                } catch (HttpClientErrorException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    eventBus.post(new OlsErrorMessageEvent(OlsErrorMessageEvent.OlsError.CONNECTION_ERROR));
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    eventBus.post(new OlsErrorMessageEvent(OlsErrorMessageEvent.OlsError.PARSE_ERROR));
                }
            } else {
                eventBus.post(new MessageEvent("Search input validation", validationMessages, JOptionPane.ERROR_MESSAGE));
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

        olsDialog.getAllOntologiesRadioButton().addActionListener(e -> {
            if (olsDialog.getAllOntologiesRadioButton().isSelected()) {
                olsDialog.getOntologiesDualList().setEnabled(false);
                olsDialog.getGetAllOntologiesButton().setEnabled(false);
            }
        });

        olsDialog.getPreselectedOntologiesRadioButton().addActionListener(e -> {
            if (olsDialog.getPreselectedOntologiesRadioButton().isSelected()) {
                olsDialog.getOntologiesDualList().setEnabled(true);
                olsDialog.getGetAllOntologiesButton().setEnabled(true);
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

        //reset the ontologies
        getOntologies();

        //reset the search fields settings if necessary
        if (olsDialog.getCustomFieldsRadioButton().isSelected()) {
            olsDialog.getDefaultFieldsRadioButton().setSelected(true);
            enableSearchFieldCheckBoxes(false);
        }

        //load the preselected ontologies
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

    /**
     * Validate the user input before performing an OLS search.
     *
     * @return the list of validation messages
     */
    private List<String> validate() {
        List<String> validationMessages = new ArrayList<>();

        String searchInput = olsDialog.getSearchInputTextField().getText();
        if (searchInput.length() < 2 || searchInput.length() > 30) {
            validationMessages.add("The search term length should be between 2 and 30");
        }
        if (olsDialog.getPreselectedOntologiesRadioButton().isSelected() && olsDialog.getOntologiesDualList().getAddedItems().isEmpty()) {
            validationMessages.add("Please select at least one ontology when searching preselected ontologies.");
        }
        if (olsDialog.getCustomFieldsRadioButton().isSelected() && getSearchFields().isEmpty()) {
            validationMessages.add("Please select at least one search field when using custom search fields.");
        }

        return validationMessages;
    }

    /**
     * Get the (preselected) ontologies and add them to the dual list if
     * necessary.
     */
    private void getOntologies() {
        if (!preselectedOntologyNamespaces.isEmpty()) {
            try {
                if (preselectedOntologies.isEmpty()) {
                    preselectedOntologies.addAll(newOlsService.getOntologiesByNamespace(preselectedOntologyNamespaces));
                }
                olsDialog.getOntologiesDualList().populateLists(olsDialog.getOntologiesDualList().getAvailableItems(), preselectedOntologies);
                olsDialog.getPreselectedOntologiesRadioButton().setSelected(true);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
                eventBus.post(new OlsErrorMessageEvent(OlsErrorMessageEvent.OlsError.PARSE_ERROR));
            } catch (HttpClientErrorException ex) {
                LOGGER.error(ex.getMessage(), ex);
                eventBus.post(new OlsErrorMessageEvent(OlsErrorMessageEvent.OlsError.CONNECTION_ERROR));
            }
        } else {
            olsDialog.getAllOntologiesRadioButton().setSelected(true);
            olsDialog.getOntologiesDualList().setEnabled(false);
        }
    }

    /**
     * MouseAdapter implementation for the search result table.
     */
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
