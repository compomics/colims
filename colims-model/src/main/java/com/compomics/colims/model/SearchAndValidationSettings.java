package com.compomics.colims.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Table(name = "search_and_validation_settings")
@Entity
public class SearchAndValidationSettings extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 3229983473906664007L;

    /**
     * The analytical run onto which the searches were performed.
     */
    @JoinColumn(name = "l_analytical_run_id", referencedColumnName = "id")
    @OneToOne
    private AnalyticalRun analyticalRun;
    /**
     * The search engine used for the searches.
     */
    @JoinColumn(name = "l_search_engine_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private SearchEngine searchEngine;
    /**
     * The search parameters.
     */
    @JoinColumn(name = "l_search_parameters_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private SearchParameters searchParameters;
    /**
     * The identification files provided by the search engine. Multiple files
     * can be linked to one SearchAndValidationSettings instance.
     */
    @OneToMany(mappedBy = "searchAndValidationSettings", cascade = javax.persistence.CascadeType.ALL)
    private List<IdentificationFile> identificationFiles = new ArrayList<>();
    /**
     * The SearchSettingsHasFastaDb instances from the join table between the
     * search and validation settings and FASTA databases.
     */
    @OneToMany(mappedBy = "searchAndValidationSettings", cascade = CascadeType.ALL)
    private List<SearchSettingsHasFastaDb> searchSettingsHasFastaDbs = new ArrayList<>();

    public List<IdentificationFile> getIdentificationFiles() {
        return identificationFiles;
    }

    public void setIdentificationFiles(List<IdentificationFile> identificationFiles) {
        this.identificationFiles = identificationFiles;
    }

    public AnalyticalRun getAnalyticalRun() {
        return analyticalRun;
    }

    public void setAnalyticalRun(AnalyticalRun analyticalRun) {
        this.analyticalRun = analyticalRun;
    }

    public SearchEngine getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameterSettings(SearchParameters searchParameterSettings) {
        this.searchParameters = searchParameterSettings;
    }

    public List<SearchSettingsHasFastaDb> getSearchSettingsHasFastaDbs() {
        return searchSettingsHasFastaDbs;
    }

    public void setSearchSettingsHasFastaDbs(List<SearchSettingsHasFastaDb> searchSettingsHasFastaDbs) {
        this.searchSettingsHasFastaDbs = searchSettingsHasFastaDbs;
    }

}
