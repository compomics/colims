
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.Material;
import java.io.Serializable;
import java.util.Comparator;


/**
 * This comparator compares name fields of Material entity instances.
 *
 * @author Niels Hulstaert
 */
public class MaterialNameComparator implements Comparator<Material>, Serializable{

    @Override
    public int compare(final Material material1, final Material material2) {
        return material1.getName().compareToIgnoreCase(material2.getName());
    }

}
