package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityWithChildrenChangeEvent;
import com.compomics.colims.model.Group;

/**
 * @author Niels Hulstaert
 */
public class GroupChangeEvent extends EntityWithChildrenChangeEvent {

    /**
     * The Group instance.
     */
    private final Group group;

    /**
     * Constructor.
     *
     * @param type the change type
     * @param childrenAffected are the child collections affected
     * @param group the Group instance
     */
    public GroupChangeEvent(final Type type, final boolean childrenAffected, final Group group) {
        super(type, childrenAffected);
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }
}
