package com.compomics.colims.core.playground;


import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.MaxQuantParser;
import com.compomics.colims.core.io.maxquant.UnparseableException;

import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;

/**
 * Created by Iain on 18/11/2014.
 */
public class MaxQuantParseAdventure {

    public static void main(String[] args) {


        ApplicationContextProvider.getInstance().setDefaultApplicationContext();
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();

        MaxQuantParser parser = applicationContext.getBean("maxQuantParser", MaxQuantParser.class);

        try {
            File blahblahblah = new File("Z:\\Davy\\maxquant\\MAXQUANT COMPOMICS\\more test compomics\\combined\\txt");

            blahblahblah.exists();

            parser.parseFolder(blahblahblah);

            System.out.println("FIN");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HeaderEnumNotInitialisedException e) {
            e.printStackTrace();
        } catch (UnparseableException e) {
            e.printStackTrace();
        } catch (MappingException e) {
            e.printStackTrace();
        }
    }
}
