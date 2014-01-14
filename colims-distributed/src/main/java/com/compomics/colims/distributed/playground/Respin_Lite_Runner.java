/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.playground;

import com.compomics.colims.distributed.config.distributedconfiguration.client.DistributedProperties;
import com.compomics.colims.distributed.searches.respin.control.common.Respin;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Kenneth
 */
public class Respin_Lite_Runner {

    private static final String userName = "admin1";
    private static final String instrumentName = "Instrument_1";
    private static final long sampleID = 1;
    private static final File distPropertiesFile = new File("C:\\Users\\Kenneth\\Documents\\Projects\\new_Colims\\colims\\colims-core\\src\\test\\resources\\distributed\\config/distribute.properties");

    private static final File fasta = new File("C:\\Users\\Kenneth\\Documents\\Projects\\new_Colims\\colims\\colims-core\\src\\test\\resources\\uniprot_sprot_101104_human_concat.fasta");
    private static final File mgf = new File("C:\\Users\\Kenneth\\Documents\\Projects\\new_Colims\\colims\\colims-core\\src\\test\\resources\\input_spectra.mgf");
    private static final File param = new File("C:\\Users\\Kenneth\\Documents\\Projects\\new_Colims\\colims\\colims-core\\src\\test\\resources\\small_scale/SearchGUI.parameters");
    private static final Respin respin = new Respin();

    public static void main(String[] args) throws IOException, Exception {
        DistributedProperties.setPropertiesFile(distPropertiesFile);
        DistributedProperties.reload();
        File outputDir = new File(DistributedProperties.getInstance().getStoragePath() + "/test/");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        respin.launch(userName, instrumentName, sampleID, mgf, param, fasta, outputDir, "test_1", false);
    }
}
