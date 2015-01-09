package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.preferences.ModificationProfile;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.*;

/**
 *
 * @author Davy
 */
public class MaxQuantParameterParserTest {

    private final File maxQuantTextFolder;

    public MaxQuantParameterParserTest() throws IOException {
        maxQuantTextFolder = new ClassPathResource("data/maxquant").getFile();
    }

    /**
     * Test of parse method, of class MaxQuantParameterParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        MaxQuantParameterParser instance = new MaxQuantParameterParser();
        instance.parse(maxQuantTextFolder);
        Map<String, SearchParameters> result = instance.getRunParameters();
        assertThat(result.keySet(), hasItem("V13967_PolyASN_test_gennaro_12h_fr10_2253_445_2_5ul"));
        //assertThat(result.get("V13967_PolyASN_test_gennaro_12h_fr10_2253_445_2_5ul").getDiscardLowQualitySpectra(), is(false));
        assertThat(result.get("V13967_PolyASN_test_gennaro_12h_fr10_2253_445_2_5ul").getFastaFile(), notNullValue());
        assertThat(result.get("V13967_PolyASN_test_gennaro_12h_fr10_2253_445_2_5ul").getFastaFile().getName(), is("SP_hum_2013_04.fasta"));
        //assertThat(result.get("V13967_PolyASN_test_gennaro_12h_fr10_2253_445_2_5ul").getMinPeptideLength(), is(7));
        //assertThat(result.get("V13967_PolyASN_test_gennaro_12h_fr10_2253_445_2_5ul").getMaxEValue(), closeTo(1.0, 0.1));
        assertThat(result.get("V13967_PolyASN_test_gennaro_12h_fr10_2253_445_2_5ul").getFragmentIonAccuracy(), closeTo(20.0, 0.1));
        assertThat(result.get("V13967_PolyASN_test_gennaro_12h_fr10_2253_445_2_5ul").getModificationProfile(), notNullValue());
        ModificationProfile testProfile = result.get("V13967_PolyASN_test_gennaro_12h_fr10_2253_445_2_5ul").getModificationProfile();
        assertThat(testProfile.getAllModifications(), is(not(empty())));
        assertThat(testProfile.getFixedModifications(), is(empty()));
        assertThat(testProfile.getVariableModifications().size(), is(2));
    }

    @Test
    public void testNewParse() throws IOException, HeaderEnumNotInitialisedException {
        MaxQuantParameterParser instance = new MaxQuantParameterParser();

        instance.parseExperiment(maxQuantTextFolder);

        System.out.println("hi");
    }
}
