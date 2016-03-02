package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.model.Spectrum;
import org.springframework.stereotype.Component;

/**
 * This class maps a MaxQuant spectrum from an apl file onto a Colims {@link com.compomics.colims.model.SpectrumFile}.
 *
 * Created by Niels Hulstaert on 28/01/16.
 */
@Component("maxQuantSpectrumFileMapper")
public class MaxQuantSpectrumFileMapper {

    public void map(Spectrum spectrum){

    }

}
