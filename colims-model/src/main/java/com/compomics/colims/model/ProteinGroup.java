package com.compomics.colims.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a protein group entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "protein_group")
@Entity
public class ProteinGroup extends DatabaseEntity {

    private static final long serialVersionUID = -8217759222711303528L;

    /**
     * The protein probability score.
     */
    @Basic(optional = true)
    @Column(name = "protein_prob", nullable = true)
    private Double proteinProbability;
    /**
     * The protein posterior error probability score.
     */
    @Basic(optional = true)
    @Column(name = "protein_post_error_prob", nullable = true)
    private Double proteinPostErrorProbability;
    /**
     * The PeptideHasProteinGroup instances from the join table between the peptide and protein group tables.
     */
    @OneToMany(mappedBy = "proteinGroup")
    private List<PeptideHasProteinGroup> peptideHasProteinGroups = new ArrayList<>();
    /**
     * The ProteinGroupHasProtein instances from the join table between the protein group and protein tables.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proteinGroup")
//    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProteinGroupHasProtein> proteinGroupHasProteins = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public ProteinGroup() {
    }

    public Double getProteinProbability() {
        return proteinProbability;
    }

    public void setProteinProbability(Double proteinProbability) {
        this.proteinProbability = proteinProbability;
    }

    public Double getProteinPostErrorProbability() {
        return proteinPostErrorProbability;
    }

    public void setProteinPostErrorProbability(Double proteinPostErrorProbability) {
        this.proteinPostErrorProbability = proteinPostErrorProbability;
    }

    public List<PeptideHasProteinGroup> getPeptideHasProteinGroups() {
        return peptideHasProteinGroups;
    }

    public void setPeptideHasProteinGroups(List<PeptideHasProteinGroup> peptideHasProteinGroups) {
        this.peptideHasProteinGroups = peptideHasProteinGroups;
    }

    public List<ProteinGroupHasProtein> getProteinGroupHasProteins() {
        return proteinGroupHasProteins;
    }

    public void setProteinGroupHasProteins(List<ProteinGroupHasProtein> proteinGroupHasProteins) {
        this.proteinGroupHasProteins = proteinGroupHasProteins;
    }

    /**
     * Get the main protein of the protein group.
     *
     * @return the main protein
     */
    public Protein getMainProtein() {
        Protein mainProtein = null;

        if (proteinGroupHasProteins.size() == 1) {
            mainProtein = proteinGroupHasProteins.get(0).getProtein();
        } else {
            for (ProteinGroupHasProtein proteinGroupHasProtein : proteinGroupHasProteins) {
                if (proteinGroupHasProtein.getIsMainGroupProtein()) {
                    mainProtein = proteinGroupHasProtein.getProtein();
                    break;
                }
            }
        }

        return mainProtein;
    }

    /**
     * Get the list of protein accessions in this group.
     *
     * @return the list of protein accession strings
     */
    public List<String> getProteinAccessions() {
        List<String> proteinAccessions = new ArrayList<>();

        for (ProteinGroupHasProtein proteinGroupHasProtein : proteinGroupHasProteins) {
            proteinAccessions.add(proteinGroupHasProtein.getProteinAccession());
        }

        return proteinAccessions;
    }
}
