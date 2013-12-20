package com.compomics.colims.core.io.parser;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.parser.impl.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.parser.impl.MaxQuantPSMParser;
import com.compomics.colims.core.io.parser.impl.MaxQuantProteinGroupParser;
import com.compomics.colims.core.io.parser.impl.MaxQuantProteinMapperStub;
import com.compomics.colims.core.io.parser.impl.UnparseableException;
import com.compomics.colims.core.mapper.MatchScore;
import com.compomics.colims.core.mapper.impl.utilitiesToColims.UtilitiesModificationMapper;
import com.compomics.colims.core.mapper.impl.utilitiesToColims.UtilitiesPeptideMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Ignore;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantColimsIdentificationInsertTest {

    @Autowired
    MaxQuantPSMParser maxQuantEvidenceParser;
    @Autowired
    MaxQuantProteinGroupParser maxQuantProteinGroupParser;
    @Autowired
    MaxQuantProteinMapperStub maxQuantProteinMapperStub;
    @Autowired
    UtilitiesModificationMapper utilitiesModificationMapper;
    @Autowired
    UtilitiesPeptideMapper utilitiesPeptideMapper;
    private SequenceFactory sequenceFactory;
    private File maxQuantEvidenceFile;
    private File maxQuantProteinGroupsFile;
    private File proteinGroupsFastaFile;

    public MaxQuantColimsIdentificationInsertTest() throws AAAAAAAAAAAAAARGHException {
        maxQuantEvidenceFile = new File(getClass().getClassLoader().getResource("testdata/evidence_subset_1000.tsv").getPath());
        maxQuantProteinGroupsFile = new File(getClass().getClassLoader().getResource("testdata/proteinGroups_subset.tsv").getPath());
        proteinGroupsFastaFile = new File(getClass().getClassLoader().getResource("testdata/testfasta.fasta").getPath());
        sequenceFactory = SequenceFactory.getInstance();
        try {
            sequenceFactory.loadFastaFile(proteinGroupsFastaFile);
        } catch (IOException | ClassNotFoundException | StringIndexOutOfBoundsException | IllegalArgumentException ex) {
            throw new AAAAAAAAAAAAAARGHException("FUUUUUUUUU");
        }
    }

    @Ignore
    @Test
    public void testIdentificationMapping() throws IOException, HeaderEnumNotInitialisedException, UnparseableException, MappingException, AAAAAAAAAAAAAARGHException {
        List<Peptide> mappedPeptides = new ArrayList<>();
        Map<Integer, PeptideAssumption> parsedPeptides = maxQuantEvidenceParser.parse(maxQuantEvidenceFile);
        final Map<Integer, ProteinMatch> parsedProteins = maxQuantProteinGroupParser.parse(maxQuantProteinGroupsFile);
        assertThat(parsedPeptides.size(), is(2408));
        //maxquantpeptidemapper
        for (final PeptideAssumption peptideAssumption : parsedPeptides.values()) {
            final Peptide colimsPeptide = new Peptide();
            final MatchScore matchScore = (MatchScore) peptideAssumption.getUrParam(new MatchScore(Double.NaN, Double.NEGATIVE_INFINITY));
            PSPtmScores ptmScores = new PSPtmScores();
            //maxquantmodificationmapper
            for (ModificationMatch match : peptideAssumption.getPeptide().getModificationMatches()) {
                //ptmScores.
                ptmScores.addMainModificationSite(match.getTheoreticPtm(), match.getModificationSite());
            }
            utilitiesPeptideMapper.map(peptideAssumption.getPeptide(), matchScore, ptmScores, colimsPeptide);
            colimsPeptide.setPeptideHasProteins(new ArrayList<PeptideHasProtein>() {
                {
                    //maxQuantproteinmapper
                    List<ProteinMatch> matches = new ArrayList<>();
                    for (String proteinAccession : peptideAssumption.getPeptide().getParentProteins()) {
                        matches.add(parsedProteins.get(Integer.parseInt(proteinAccession)));
                    }
                    PeptideHasProtein php = new PeptideHasProtein();
                    Protein protein = new Protein();
                    try {
                        maxQuantProteinMapperStub.map(matches, matchScore, colimsPeptide);
                    } catch (Exception e) {

                        throw new AAAAAAAAAAAAAARGHException("EEEEEUUUUUUUUUUUUUUCH\n" + e.getMessage());
                    }
                    php.setProtein(protein);
                    php.setPeptide(colimsPeptide);
                    this.add(php);
                }
            });
            mappedPeptides.add(colimsPeptide);
        }
        assertThat(mappedPeptides.size(), is(2408));
        //assertThat(mappedPeptides.get(408).getPsmPostErrorProbability(),closeTo(-1.1,0.1));
        //TODO: assert that a parsed peptide is actually a parsed peptide
    }

    private static class AAAAAAAAAAAAAARGHException extends Exception {

        public AAAAAAAAAAAAAARGHException(String fuuuuuuuuu) {
            super(fuuuuuuuuu);
        }
    }
}
