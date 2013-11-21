/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.exception.MappingException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import com.compomics.colims.core.io.IOManager;
import com.compomics.colims.core.io.parser.MzMLParser;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.model.ExperimentBinaryFile;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.repository.ExperimentRepository;
import java.util.logging.Level;

/**
 *
 * @author Niels Hulstaert
 */
@Service("experimentService")
@Transactional
public class ExperimentServiceImpl implements ExperimentService {

    private static final Logger LOGGER = Logger.getLogger(ExperimentServiceImpl.class);
    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private MzMLParser mzMLIOService;
    @Autowired
    private IOManager ioManager;

    @Override
    public Experiment findById(Long id) {
        return experimentRepository.findById(id);
    }

    @Override
    public List<Experiment> findAll() {
        return experimentRepository.findAll();
    }

    @Override
    public void save(Experiment entity) {
        experimentRepository.save(entity);
    }

    @Override
    public void delete(Experiment entity) {
        experimentRepository.delete(entity);
    }

    @Override
    public void importMzMlExperiments(List<File> mzMlFiles) throws IOException {
        //import files in mzMLIOService
        mzMLIOService.importMzMLFiles(mzMlFiles);

        //iterate over the files and parse them
        for (File mzMLFile : mzMlFiles) {
            Experiment experiment = new Experiment();
            try {
                experiment = mzMLIOService.parseMzmlFile(mzMLFile.getName());
            } catch (MzMLUnmarshallerException | MappingException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            experiment.setBinaryFiles(new ArrayList<ExperimentBinaryFile>());

            //add the file to the Experiment entity as BinaryFile entity
            ExperimentBinaryFile binaryFile = new ExperimentBinaryFile(ioManager.readBytesFromFile(mzMLFile));
            experiment.getBinaryFiles().add(binaryFile);

            //save experiment to db
            experimentRepository.save(experiment);
        }
    }

    @Override
    public void update(Experiment entity) {
        experimentRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(Experiment entity) {
        experimentRepository.saveOrUpdate(entity);
    }
}
