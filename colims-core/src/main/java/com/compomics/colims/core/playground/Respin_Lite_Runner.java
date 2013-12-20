/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.playground;

import com.compomics.colims.core.config.distributedconfiguration.client.DistributedProperties;
import com.compomics.colims.core.distributed.searches.respin.control.common.Respin;
import com.compomics.colims.core.distributed.searches.respin.model.exception.RespinException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Kenneth
 */
public class Respin_Lite_Runner {

    private static final String userName = "admin1";
    private static final String instrumentName = "Instrument_1";
    private static final long sampleID = 1;
    private static File distPropertiesFile = new File("C:\\Users\\Kenneth\\Documents\\Projects\\new_Colims\\colims\\colims-core\\src\\test\\resources\\distributed\\config/distribute.properties");

    private static File fasta = new File("C:\\Users\\Kenneth\\Documents\\Projects\\new_Colims\\colims\\colims-core\\src\\test\\resources\\uniprot_sprot_101104_human_concat.fasta");
    private static File mgf = new File("C:\\Users\\Kenneth\\Documents\\Projects\\new_Colims\\colims\\colims-core\\src\\test\\resources\\input_spectra.mgf");
    private static File param = new File("C:\\Users\\Kenneth\\Documents\\Projects\\new_Colims\\colims\\colims-core\\src\\test\\resources\\small_scale/SearchGUI.parameters");
    private static Respin respin = new Respin();

    public static void main(String[] args) throws IOException, Exception {
        DistributedProperties.setPropertiesFile(distPropertiesFile);
        DistributedProperties.reload();
        File outputDir = new File(DistributedProperties.getInstance().getStoragePath() + "/test/");
        if(!outputDir.exists()){
            outputDir.mkdirs();
        }
        PrintWriter writer = new PrintWriter(new File(outputDir, "output.txt"));
        respin.launch(userName, instrumentName, sampleID, mgf, param, fasta, outputDir, "test_1", writer, false);
    }
}
