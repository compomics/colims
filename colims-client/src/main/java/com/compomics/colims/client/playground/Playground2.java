package com.compomics.colims.client.playground;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.model.Modification;
import org.apache.commons.compress.archivers.ArchiveException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xmlpull.v1.XmlPullParserException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import javax.jms.JMSException;
import javax.management.openmbean.OpenDataException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * @author Niels Hulstaert
 */
public class Playground2 {

    public static void main(String[] args) throws IOException, MappingException, SQLException, ClassNotFoundException, InterruptedException, IllegalArgumentException, MzMLUnmarshallerException, XmlPullParserException, ArchiveException, JMSException, OpenDataException {

        UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        Object get = lookAndFeelDefaults.get("[Selected].background");

        for(Object testing : lookAndFeelDefaults.keySet()){
            String teString = testing.toString();
            if(teString.contains("textHighlight")){
                System.out.println(teString);
                System.out.println("value: " + lookAndFeelDefaults.get(teString));
            }
        }

        System.out.println("test");
    }
}
