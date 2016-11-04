package com.compomics.colims.model;

import com.compomics.colims.model.cv.CvParam;
import com.compomics.colims.model.enums.SearchEngineType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a search engine entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_engine")
@Entity
public class SearchEngine extends CvParam {

    private static final long serialVersionUID = -5428696863055618148L;
    private static final String NOT_APPLICABLE = "N/A";

    /**
     * The search engine type.
     */
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SearchEngineType searchEngineType;
    /**
     * The version of the search engine.
     */
    @Basic(optional = true)
    @Column(name = "version", nullable = true)
    private String version;
    /**
     * The search and validation settings of the runs searched by this search engine.
     */
    @OneToMany(mappedBy = "searchEngine")
    private List<SearchAndValidationSettings> searchAndValidationSettingses = new ArrayList<>();

    /**
     * No arg constructor.
     */
    public SearchEngine() {
    }

    /**
     * Constructor.
     *
     * @param searchEngineType the search engine type enum
     * @param version          the search engine version
     */
    public SearchEngine(final SearchEngineType searchEngineType, final String version) {
        super(NOT_APPLICABLE, NOT_APPLICABLE, NOT_APPLICABLE);
        this.searchEngineType = searchEngineType;
        this.version = version;
    }

    /**
     * Constructor.
     *
     * @param searchEngineType the search engine type enum
     * @param version          the search engine version
     * @param label            the CV term label
     * @param accession        The CV term accession
     * @param name             The CV term name
     */
    public SearchEngine(final SearchEngineType searchEngineType, final String version, final String label, final String accession, final String name) {
        super(label, accession, name);
        this.searchEngineType = searchEngineType;
        this.version = version;
    }

    /**
     * Constructor that creates a new instance with all fields of the given SearchEngine and the given version.
     *
     * @param searchEngine the SearchEngine to copy
     * @param version      the search engine version
     */
    public SearchEngine(final SearchEngine searchEngine, final String version) {
        this(searchEngine.getSearchEngineType(), version, searchEngine.getLabel(), searchEngine.getAccession(), searchEngine.getName());
    }

    public SearchEngineType getSearchEngineType() {
        return searchEngineType;
    }

    public void setSearchEngineType(SearchEngineType searchEngineType) {
        this.searchEngineType = searchEngineType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<SearchAndValidationSettings> getSearchAndValidationSettingses() {
        return searchAndValidationSettingses;
    }

    public void setSearchAndValidationSettingses(List<SearchAndValidationSettings> searchAndValidationSettingses) {
        this.searchAndValidationSettingses = searchAndValidationSettingses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchEngine that = (SearchEngine) o;

        if (searchEngineType != that.searchEngineType) return false;
        return !(version != null ? !version.equals(that.version) : that.version != null);

    }

    @Override
    public int hashCode() {
        int result = searchEngineType.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
