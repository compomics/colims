package com.compomics.colims.model;

import javax.persistence.*;

/**
 * This class represents the join table between the peptide and protein tables.
 *
 * @author Niels Hulstaert
 */
@Table(name = "protein_group_has_protein")
@Entity
public class ProteinGroupHasProtein extends DatabaseEntity {

    private static final long serialVersionUID = -7522445376198555037L;

    /**
     * Boolean field to keep track of the main protein of the protein group. If there's only one protein, this field
     * will be set to true.
     */
    @Basic(optional = false)
    @Column(name = "main_group_protein", nullable = false)
    private Boolean isMainGroupProtein = false;
    /**
     * The protein accession. It's important to note that the accession is also added (if not already present) to the
     * {@link ProteinAccession} table. It's stored as well here for auditing purposes.
     */
    @Basic(optional = true)
    @Column(name = "protein_accession", nullable = true)
    private String proteinAccession;
    /**
     * The ProteinGroup instance of this join entity.
     */
    @JoinColumn(name = "l_protein_group_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProteinGroup proteinGroup;
    /**
     * The Protein instance of this join entity.
     */
    @JoinColumn(name = "l_protein_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Protein protein;

    public Boolean getIsMainGroupProtein() {
        return isMainGroupProtein;
    }

    public void setIsMainGroupProtein(Boolean isMainGroupProtein) {
        this.isMainGroupProtein = isMainGroupProtein;
    }

    public String getProteinAccession() {
        return proteinAccession;
    }

    public void setProteinAccession(String proteinAccession) {
        this.proteinAccession = proteinAccession;
    }

    public ProteinGroup getProteinGroup() {
        return proteinGroup;
    }

    public void setProteinGroup(ProteinGroup proteinGroup) {
        this.proteinGroup = proteinGroup;
    }

    public Protein getProtein() {
        return protein;
    }

    public void setProtein(final Protein protein) {
        this.protein = protein;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProteinGroupHasProtein that = (ProteinGroupHasProtein) o;

        if (!isMainGroupProtein.equals(that.isMainGroupProtein)) return false;
        if (proteinAccession != null ? !proteinAccession.equals(that.proteinAccession) : that.proteinAccession != null)
            return false;
        if (!proteinGroup.equals(that.proteinGroup)) return false;
        return protein.equals(that.protein);

    }

    @Override
    public int hashCode() {
        int result = isMainGroupProtein.hashCode();
        result = 31 * result + (proteinAccession != null ? proteinAccession.hashCode() : 0);
        result = 31 * result + proteinGroup.hashCode();
        result = 31 * result + protein.hashCode();
        return result;
    }
}
