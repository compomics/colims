package com.compomics.colims.core.io;

import java.io.Serializable;
import org.springframework.core.io.Resource;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class DataImport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The fasta resource
     */
    protected Resource fastaResource;

    public DataImport() {
    }

    public DataImport(Resource fastaResource) {
        this.fastaResource = fastaResource;
    }

    public Resource getFastaResource() {
        return fastaResource;
    }

    public void setFastaResource(Resource fastaResource) {
        this.fastaResource = fastaResource;
    }        

}
