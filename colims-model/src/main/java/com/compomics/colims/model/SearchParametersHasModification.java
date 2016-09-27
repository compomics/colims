package com.compomics.colims.model;

import com.compomics.colims.model.enums.ModificationType;

import javax.persistence.*;

/**
 * @author Niels Hulstaert
 */
@Table(name = "search_params_has_modification")
@Entity
public class SearchParametersHasModification extends DatabaseEntity {

    private static final long serialVersionUID = -4450629780383189785L;

    /**
     * The modification type (fixed, variable).
     */
    @Basic(optional = true)
    @Column(name = "modification_type", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private ModificationType modificationType;
    /**
     * The SearchParameters instance of this join entity.
     */
    @JoinColumn(name = "l_search_parameters_id", referencedColumnName = "id")
    @ManyToOne
    private SearchParameters searchParameters;
    /**
     * The SearchModification instance of this join entity.
     */
    @JoinColumn(name = "l_search_modification_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private SearchModification searchModification;

    /**
     * No-arg constructor.
     */
    public SearchParametersHasModification() {
    }

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(SearchParameters searchParameters) {
        this.searchParameters = searchParameters;
    }

    public SearchModification getSearchModification() {
        return searchModification;
    }

    public void setSearchModification(SearchModification searchModification) {
        this.searchModification = searchModification;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(final ModificationType modificationType) {
        this.modificationType = modificationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchParametersHasModification that = (SearchParametersHasModification) o;

        if (modificationType != that.modificationType) return false;
        return !(searchModification != null ? !searchModification.equals(that.searchModification) : that.searchModification != null);

    }

    @Override
    public int hashCode() {
        int result = modificationType != null ? modificationType.hashCode() : 0;
        result = 31 * result + (searchModification != null ? searchModification.hashCode() : 0);
        return result;
    }
}
