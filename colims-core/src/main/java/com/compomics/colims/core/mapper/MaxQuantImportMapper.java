package com.compomics.colims.core.mapper;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.parser.impl.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.parser.impl.MaxQuantAnalyticalRun;
import com.compomics.colims.core.io.parser.impl.MaxQuantParser;
import com.compomics.colims.core.io.parser.impl.UnparseableException;
import com.compomics.colims.core.io.parser.model.MaxQuantImport;
import com.compomics.colims.core.mapper.impl.MaxQuantToColims.MaxQuantUtilitiesAnalyticalRunMapper;
import com.compomics.colims.core.mapper.impl.MaxQuantToColims.MaxQuantUtilitiesPsmMapper;
import com.compomics.colims.core.mapper.impl.utilitiesToColims.UtilitiesPsmMapper;
import com.compomics.colims.core.mapper.impl.utilitiesToColims.UtilitiesSpectrumMapper;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Davy
 */
public class MaxQuantImportMapper {

    private static final Logger LOGGER = Logger.getLogger(PeptideShakerImportMapper.class);
    @Autowired
    private UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    @Autowired
    private UtilitiesPsmMapper utilitiesPsmMapper;
    @Autowired
    private MaxQuantParser maxQuantParser;
    @Autowired
    private MaxQuantUtilitiesAnalyticalRunMapper maxQuantUtilitiesAnalyticalRunMapper;
    @Autowired
    private MaxQuantUtilitiesPsmMapper maxQuantUtilitiesPsmMapper;
    
    /**
     * method to import a max quant search into colims
     *
     * @param aMaxQuantImport the resulting files from the max quant search and
     * the fasta ran against
     * @param sampleRanThroughMaxQuant the sample we should store against since
     * there is no way of inferring which sample was run from the files alone
     * @return the list of analytical runs that were mapped to the sample
     * @throws IOException if something went wrong while trying to read files
     * from the folder
     * @throws UnparseableException if a file is missing crucial information
     * @throws HeaderEnumNotInitialisedException this means that a header was
     * added to the header enum that did not have a possible header name
     * @throws MappingException if a mapping could not be completed
     */
    public List<AnalyticalRun> map(MaxQuantImport aMaxQuantImport) throws IOException, UnparseableException, HeaderEnumNotInitialisedException, MappingException {
        LOGGER.info("started mapping folder: " + aMaxQuantImport.getMaxQuantFolder().getName());
        List<AnalyticalRun> mappedRuns = new ArrayList<>();
        
        //just in case
        maxQuantParser.clearParsedProject();
        
        maxQuantParser.parseMaxQuantTextFolder(aMaxQuantImport.getMaxQuantFolder());
        
        for (MaxQuantAnalyticalRun aParsedRun : maxQuantParser.getRuns()) {
            AnalyticalRun targetRun = new AnalyticalRun();
            
            maxQuantUtilitiesAnalyticalRunMapper.map(aParsedRun, targetRun);
            
            List<Spectrum> mappedSpectra = new ArrayList<>(aParsedRun.getListOfSpectra().size());
            
            for (MSnSpectrum aParsedSpectrum : aParsedRun.getListOfSpectra()) {
                Spectrum targetSpectrum = new Spectrum();
                
                //for the spectra we can just use the standard utilities mapper
                //@TODO get the fragmentation type 
                utilitiesSpectrumMapper.map(aParsedSpectrum, null, targetSpectrum);
                mappedSpectra.add(targetSpectrum);
                
                //
                maxQuantUtilitiesPsmMapper.map(aParsedSpectrum,maxQuantParser,targetSpectrum);
            }
            targetRun.setSpectrums(mappedSpectra);
        }

        return mappedRuns;
    }
}
