package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.UserQueryService;
import com.compomics.colims.model.UserQuery;
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

    @Override
    public UserQuery findByUserIdAndQueryString(Long userId, String queryString) {
        return userQueryRepository.findByUserIdAndQueryString(userId, queryString);
    }

    @Override
    public UserQuery merge(UserQuery userQuery) {
        return userQueryRepository.merge(userQuery);
    }

    @Override
    public UserQuery findById(Long aLong) {
        return userQueryRepository.findById(aLong);
    }

    @Override
    public List<UserQuery> findAll() {
        return userQueryRepository.findAll();
    }

    @Override
    public void save(UserQuery entity) {
        userQueryRepository.persist(entity);
    }

    @Override
    public void update(UserQuery entity) {

    }

    @Override
    public void saveOrUpdate(UserQuery entity) {

    }

    @Override
    public void delete(UserQuery entity) {
        userQueryRepository.remove(entity);
    }

    @Override
    public long countAll() {
        return userQueryRepository.countAll();
    }
}
