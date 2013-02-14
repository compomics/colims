package com.compomics.colims.core.playground;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentParam;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.ProjectParam;
import com.compomics.colims.model.User;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Niels Hulstaert
 */
public class PlayGround {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-core-context.xml");

        InstrumentService instrumentService = (InstrumentService) applicationContext.getBean("instrumentService");

        Instrument instrument_1 = new Instrument("instrument_1");
        instrument_1.setDescription("instrument 1 description");
        instrument_1.setUsername("testUser");

        List<InstrumentParam> instrumentParams_1 = new ArrayList<InstrumentParam>();
        InstrumentParam instrumentParam_1 = new InstrumentParam();
        instrumentParam_1.setAccession("accession_1");
        instrumentParam_1.setValue("instrument_param_value_1");
        instrumentParam_1.setUsername("testUser");
        instrumentParam_1.setCvLabel("cv_label_1");
        instrumentParam_1.setInstrument(instrument_1);
        instrumentParams_1.add(instrumentParam_1);

        InstrumentParam instrumentParam_2 = new InstrumentParam();
        instrumentParam_2.setAccession("accession_2");
        instrumentParam_2.setValue("instrument_param_value_2");
        instrumentParam_2.setUsername("testUser");
        instrumentParam_2.setCvLabel("cv_label_1");
        instrumentParam_2.setInstrument(instrument_1);
        instrumentParams_1.add(instrumentParam_2);

        instrument_1.setInstrumentParams(instrumentParams_1);

        Instrument instrument_2 = new Instrument("instrument_2");
        instrument_2.setDescription("instrument 2 description");
        instrument_2.setUsername("testUser");

        List<InstrumentParam> instrumentParams_2 = new ArrayList<InstrumentParam>();
        InstrumentParam instrumentParam_3 = new InstrumentParam();
        instrumentParam_3.setAccession("accession_3");
        instrumentParam_3.setValue("instrument_param_value_3");
        instrumentParam_3.setUsername("testUser");
        instrumentParam_3.setCvLabel("cv_label_1");
        instrumentParam_3.setInstrument(instrument_2);
        instrumentParams_2.add(instrumentParam_3);

        instrument_2.setInstrumentParams(instrumentParams_2);

        System.out.println("number of instruments before: " + instrumentService.findAll().size());

        instrumentService.save(instrument_1);
        instrumentService.save(instrument_2);

        System.out.println("number of instruments before: " + instrumentService.findAll().size());
        
        Instrument testing = instrumentService.findByName("instrument_1");
        testing.setName("instrument_11");
        instrumentService.update(testing);
    }
}
