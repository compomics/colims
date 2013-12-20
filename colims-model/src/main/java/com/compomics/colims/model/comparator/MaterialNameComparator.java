
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.Material;
import java.io.Serializable;
import java.util.Comparator;


/**
 *
 * @author Niels Hulstaert
 */
public class MaterialNameComparator implements Comparator<Material>, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(Material material1, Material material2) {
        return material1.getName().compareToIgnoreCase(material2.getName());
    }

}
