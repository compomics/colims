package com.compomics.colims.core.service;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.mapper.MatchScore;
import com.compomics.colims.core.mapper.impl.utilitiesToColims.UtilitiesPeptideMapper;
import com.compomics.colims.core.mapper.impl.utilitiesToColims.UtilitiesProteinMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class PeptideServiceTest {

    @Autowired
    private PeptideService peptideService;
    @Autowired
    private UserService userService;
    @Autowired
    private UtilitiesProteinMapper utilitiesProteinMapper;
    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;
    @Autowired
    private AuthenticationBean authenticationBean;

    @BeforeClass
    public static void setupOnce() throws IOException, FileNotFoundException, ClassNotFoundException {
        //load SequenceFactory for testing
        File fastaFile = new ClassPathResource("uniprot_sprot_101104_human_concat.fasta").getFile();
        SequenceFactory.getInstance().loadFastaFile(fastaFile);
    }

    @Test
    public void testPersistPeptideWithAlreadyPersistedProteins() throws MappingException {
        //set admin user in authentication bean
        User adminUser = userService.findByName("admin1");
        authenticationBean.setCurrentUser(adminUser);

        //create new utilities peptide
        ArrayList<String> parentProteins = new ArrayList<>();
        parentProteins.add("Q8IWA5");
        parentProteins.add("Q13233");
        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", parentProteins, new ArrayList<ModificationMatch>());

        Peptide targetPeptide = new Peptide();

        //create utilities protein matches
        List<ProteinMatch> proteinMatches = new ArrayList();
        ProteinMatch proteinMatch = new ProteinMatch("Q8IWA5");
        proteinMatches.add(proteinMatch);
        proteinMatch = new ProteinMatch("Q13233");
        proteinMatches.add(proteinMatch);

        //create peptide scores
        MatchScore peptideMatchScore = new MatchScore(0.5, 0.1);

        utilitiesProteinMapper.map(proteinMatches, peptideMatchScore, targetPeptide);
        utilitiesPeptideMapper.map(sourcePeptide, peptideMatchScore, null, targetPeptide);

        peptideService.save(targetPeptide);
    }

}
