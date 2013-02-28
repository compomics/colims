/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Header.DatabaseType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "protein")
@Entity
public class Protein extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "accession")
    private String accession;
    @Lob
    @Basic(optional = false)
    @Column(name = "protein_sequence")
    private String sequence;
    @Basic(optional = false)
    @Column(name = "database_type")
    @Enumerated(EnumType.STRING)
    private Header.DatabaseType databaseType;
    @OneToMany(mappedBy = "protein")
    private List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();

    public Protein() {
    }

    public Protein(String accession, String sequence, DatabaseType databaseType) {
        this.accession = accession;
        this.sequence = sequence;
        this.databaseType = databaseType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public List<PeptideHasProtein> getPeptideHasProteins() {
        return peptideHasProteins;
    }

    public void setPeptideHasProteins(List<PeptideHasProtein> peptideHasProteins) {
        this.peptideHasProteins = peptideHasProteins;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.accession != null ? this.accession.hashCode() : 0);
        hash = 67 * hash + (this.databaseType != null ? this.databaseType.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Protein other = (Protein) obj;
        if ((this.accession == null) ? (other.accession != null) : !this.accession.equals(other.accession)) {
            return false;
        }
        if ((this.sequence == null) ? (other.sequence != null) : !this.sequence.equals(other.sequence)) {
            return false;
        }
        if (this.databaseType != other.databaseType) {
            return false;
        }
        return true;
    }
}
