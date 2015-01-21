package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Niels Hulstaert
 */
@Table(name = "search_and_validation_settings")
@Entity
public class SearchAndValidationSettings extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 3229983473906664007L;

    /**
     * The identification files provided by the search engine. Multiple files can be linked to one
     * SearchAndValidationSettings instance.
     */
    @OneToMany(mappedBy = "searchAndValidationSettings")
    private List<IdentificationFile> identificationFiles = new ArrayList<>();
    /**
     * The analytical run onto which the searches were performed.
     */
    @JoinColumn(name = "l_analytical_run_id", referencedColumnName = "id")
    @ManyToOne
    private AnalyticalRun analyticalRun;
    /**
     * The search engine used for the searches.
     */
    @JoinColumn(name = "l_search_engine_id", referencedColumnName = "id")
    @ManyToOne
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private SearchEngine searchEngine;
    /**
     * The FASTA file used for the searches.
     */
    @JoinColumn(name = "l_fasta_db_id", referencedColumnName = "id")
    @ManyToOne
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private FastaDb fastaDb;
    /**
     * The search parameters.
     */
    @JoinColumn(name = "l_search_parameters_id", referencedColumnName = "id")
    @ManyToOne
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private SearchParameters searchParameters;

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

    public FastaDb getFastaDb() {
        return fastaDb;
    }

    public void setFastaDb(FastaDb fastaDb) {
        this.fastaDb = fastaDb;
    }

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameterSettings(SearchParameters searchParameterSettings) {
        this.searchParameters = searchParameterSettings;
    }

}