/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage.processing.colimsimport.impl;

import com.compomics.colims.distributed.storage.processing.colimsimport.ColimsFileImporter;
import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.model.PeptideShakerImport;
import com.compomics.colims.core.mapper.Mapper;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("colimsMaxQuantImporter")
public class ColimsMaxQuantImporter implements ColimsFileImporter {


    /**
     *
     * @param quantFolder the folder where the resultfiles of the quant search are located in
     * @return if the folder contains all required files
     */
    @Override
    public boolean validate(File quantFolder){
        return true;
    }
    
    @Override
    public void storeFile(String username, File cpsFileFolder,long sampleId,String instrumentName) throws PeptideShakerIOException, MappingException {
       
        }
    }

