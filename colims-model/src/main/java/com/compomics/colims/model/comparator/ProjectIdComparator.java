
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.Project;
import java.io.Serializable;
import java.util.Comparator;


/**
 *
 * @author Niels Hulstaert
 */
public class ProjectIdComparator implements Comparator<Project>, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(Project o1, Project o2) {
        return Long.compare(o1.getId(), o2.getId());
    }

}
