package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.UserQueryService;
import com.compomics.colims.repository.UserQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Niels Hulstaert on 5/11/15.
 */
@Service("userQueryService")
@Transactional
public class UserQueryServiceImpl implements UserQueryService {

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Override
    public List<LinkedHashMap<String, Object>> executeQuery(String queryString) {
        return userQueryRepository.executeQuery(queryString);
    }
}
