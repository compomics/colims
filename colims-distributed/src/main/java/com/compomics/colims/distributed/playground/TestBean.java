package com.compomics.colims.distributed.playground;

import java.io.Serializable;

/**
 *
 * @author Niels Hulstaert
 */
public class TestBean implements Serializable {

    private static final long serialVersionUID = 123L;
    private String name;
    private String address;

    public TestBean() {
    }

    public TestBean(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "TestBean{" + "name=" + name + ", address=" + address + '}';
    }       

}
