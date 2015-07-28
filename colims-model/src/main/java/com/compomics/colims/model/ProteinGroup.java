package com.compomics.colims.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
     * The PeptideHasProteinGroup instances from the join table between the peptide and protein group tables.
     */
    @OneToMany(mappedBy = "proteinGroup")
    private List<PeptideHasProteinGroup> peptideHasProteinGroups = new ArrayList<>();
    /**
     * The ProteinGroupHasProtein instances from the join table between the protein group and protein tables.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proteinGroup")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProteinGroupHasProtein> proteinGroupHasProteins = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public ProteinGroup() {
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
