package com.compomics.colims.core.playground;

import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.io.colims_to_utilities.ColimsPeptideMapper;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.repository.hibernate.PeptideMzTabDTO;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
public class Playground2 {

    public static void main(String[] args) throws IOException {
        ApplicationContextProvider.getInstance().setDefaultApplicationContext();
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();

//        UserService userService = (UserService) applicationContext.getBean("userService");
//        User user = userService.findById(1L);
        PeptideService peptideService = (PeptideService) applicationContext.getBean("peptideService");
        ColimsPeptideMapper colimsPeptideMapper = (ColimsPeptideMapper) applicationContext.getBean("colimsPeptideMapper");
        List<Long> runIds = new ArrayList<>();
        runIds.add(1L);
        runIds.add(2L);
        runIds.add(3L);
        List<PeptideMzTabDTO> peptideMzTabDTOs = peptideService.getPeptideMzTabDTOs(runIds);

        for (PeptideMzTabDTO peptideMzTabDTO : peptideMzTabDTOs) {
            colimsPeptideMapper.mapFragmentAnnotations(peptideMzTabDTO.getPeptide());
        }

//        UserQueryService userQueryService = (UserQueryService) applicationContext.getBean("userQueryService");
//        List<LinkedHashMap<String, Object>> linkedHashMaps = userQueryService.executeUserQuery(user, "select * from project");


        System.out.println("test");
    }

}
