package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class MaxQuantMsmsParserTest {

    File msmsFile;

    public MaxQuantMsmsParserTest() {
        msmsFile = new File(getClass().getClassLoader().getResource("testdata/msms_subset_1000.tsv").getPath());

    }

    /**
     * Test of parse method, of class MaxQuantMsmsParser.
     */
    @Test
    public void testParseWithoutPeaklist() throws Exception {
        System.out.println("parseWithoutPeaklist");
        boolean addPeakList = false;
        MaxQuantMsmsParser instance = new MaxQuantMsmsParser();
        Map<Integer, MSnSpectrum> result = instance.parse(msmsFile, addPeakList);
        assertThat(result.keySet().size(), is(999));
        assertThat(Integer.parseInt(result.get(899).getScanNumber()), is(58732));
        assertThat(result.get(899).getSpectrumTitle(), is("QE2_120326_OPL2023_stoolpools_MdW_LB_stoolpool1_02-58732"));
        assertThat(result.get(899).getPrecursor().getRt(), is(145.76));
        assertThat(result.get(899).getPrecursor().getPossibleCharges().size(), is(1));
        assertThat(result.get(899).getPrecursor().getPossibleCharges().get(0).getChargeAsFormattedString(), is("++"));
        assertThat(result.get(899).getPrecursor().getMz(), is(993.51256));
        assertThat(result.get(899).getPrecursor().getIntensity(), is(0.0));
        boolean mgfFail = false;
        try {
            result.get(899).asMgf();
        } catch (NullPointerException npe) {
            mgfFail = true;
        }
        assertThat(mgfFail, is(true));
    }

    @Test
    public void testParseWithPeaklist() throws Exception {
        System.out.println("parseWithPeaklist");
        boolean addPeakList = true;
        MaxQuantMsmsParser instance = new MaxQuantMsmsParser();
        Map<Integer, MSnSpectrum> result = instance.parse(msmsFile, addPeakList);
        assertThat(result.keySet().size(), is(999));
        assertThat(result.get(899).getPeakList().size(), is(22));
        Iterator<Peak> peaks = result.get(899).getPeakList().iterator();
        //please note that the peaks are not in the order found in the file but in the natural order from the underlying hashmap
        Peak peak = peaks.next();
        assertThat(peak.getMz(), is(1005.48413188591));
        assertThat(peak.getIntensity(), is(34416.9));
    }
}
