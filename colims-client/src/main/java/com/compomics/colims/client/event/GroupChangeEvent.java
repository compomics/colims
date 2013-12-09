package com.compomics.colims.client.event;

import com.compomics.colims.model.Group;

/**
 * @author Niels Hulstaert
 */
public class GroupChangeEvent extends EntityChangeEvent {

    private Group group;

    public GroupChangeEvent(Type type, boolean childrenAffected, Group group) {
        super(type, childrenAffected);
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }
}
