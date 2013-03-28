package com.compomics.colims.client.event;

import com.compomics.colims.model.Group;

/**
 * @author Niels Hulstaert
 */
public class GroupChangeEvent extends EntityChangeEvent {

    private Group group;

    public GroupChangeEvent(Type type, Group group) {
        this.group = group;
        this.type = type;
    }

    public Group getGroup() {
        return group;
    }
}
