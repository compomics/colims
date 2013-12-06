/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.compomics.colims.model.Experiment;

/**
 *
 * @author Niels Hulstaert
 */
public interface AuthenticationService {

    /**
     * Check if the currently logged in user has the permission to delete.
     *
     * @return true if the user has the permission to delete
     */
    boolean hasDeletePermission();
}
