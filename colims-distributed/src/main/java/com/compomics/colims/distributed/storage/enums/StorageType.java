/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage.enums;

/**
 *
 * @author Kenneth
 */
public enum StorageType {

    RESPIN("respin"), PEPTIDESHAKER("PeptideShaker"), MAX_QUANT("MaxQuant");

    /**
     * The user friendly name of the storage type
     */
    private final String userFriendlyName;

    private StorageType(final String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public String userFriendlyName() {
        return userFriendlyName;
    }

    /**
     * Get the StorageType enum by its user friendly name. Return null if no
     * enum value could be matched.
     *
     * @param userFriendlyName
     * @return the StorageType enum
     */
    public static StorageType getByUserFriendlyName(String userFriendlyName) {
        StorageType foundStorageType = null;
        
        //iterate over enum values
        for(StorageType storageType : values()){
            if(storageType.userFriendlyName.equals(userFriendlyName)){
                foundStorageType = storageType;
            }
        }
        
        return foundStorageType;
    }

}
