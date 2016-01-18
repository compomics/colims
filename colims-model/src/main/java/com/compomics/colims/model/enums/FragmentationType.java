package com.compomics.colims.model.enums;

/**
 * This enum contains the different fragmentation types.
 *
 * @author Niels Hulstaert
 */
public enum FragmentationType {

    /**
     * Collision-induced dissociation.
     */
    CID,
    /**
     * Electron-transfer dissociation.
     */
    EDT,
    /**
     * Higher-energy C-trap dissociation.
     */
    HCD,
    /**
     * Electron-transfer/Higher-energy C-trap hybrid dissociation.
     */
    ETHCD,
    /**
     * Electron-transfer/collision-induced dissociation.
     */
    ETCID,
    /**
     * Unknown fragmentation type.
     */
    UNKNOWN

}
