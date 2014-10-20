package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "modification")
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Modification extends AbstractModification {

    private static final long serialVersionUID = 497141602900321901L;

    @OneToMany(mappedBy = "modification")
    private List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

    public Modification(String name) {
        super(name);
    }

    public Modification(String accession, String name) {
        super(accession, name);
    }

    public List<PeptideHasModification> getPeptideHasModifications() {
        return peptideHasModifications;
    }

    public void setPeptideHasModifications(List<PeptideHasModification> peptideHasModifications) {
        this.peptideHasModifications = peptideHasModifications;
    }

}
