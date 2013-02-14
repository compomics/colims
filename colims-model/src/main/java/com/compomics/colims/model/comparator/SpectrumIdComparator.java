
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.Spectrum;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class SpectrumIdComparator implements Comparator<Spectrum>, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(Spectrum o1, Spectrum o2) {
        return Long.compare(o1.getId(), o2.getId());
    }

}
