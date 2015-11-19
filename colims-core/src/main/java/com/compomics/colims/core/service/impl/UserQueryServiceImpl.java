package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.UserQueryService;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserQuery;
import com.compomics.colims.repository.UserQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * The maximum number of results returned by the query.
     */
    @Value("${user_query.max_number_results}")
    private Integer maximumNumberOfResults;
    /**
     * The maximum number of user queries to store.
     */
    @Value("${user_query.max_number_store}")
    private Integer maximumNumberToStore;

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Override
    public List<LinkedHashMap<String, Object>> executeUserQuery(User user, String queryString) {
        List<LinkedHashMap<String, Object>> results = userQueryRepository.executeUserQuery(queryString, maximumNumberOfResults);

        Long numberOfUserQueries = userQueryRepository.countByUserId(user.getId());
        UserQuery foundUserQuery = userQueryRepository.findByUserIdAndQueryString(user.getId(), queryString);

        if (foundUserQuery != null) {
            foundUserQuery.setUsageCount(foundUserQuery.getUsageCount() + 1);
        } else {
            //check the number of stored queries against the maximum
            if (numberOfUserQueries >= maximumNumberToStore) {
                userQueryRepository.removeLeastUsedUserQuery(user.getId());
            }
            //persist new user query
            UserQuery userQuery = new UserQuery(queryString);
            userQuery.setUser(user);
            persist(userQuery);
        }

        return results;
    }

    @Override
    public List<UserQuery> findByUserId(Long userId) {
        return userQueryRepository.findByUserId(userId);
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
    public void persist(UserQuery entity) {
        userQueryRepository.persist(entity);
    }

    @Override
    public void remove(UserQuery entity) {
        userQueryRepository.remove(entity);
    }

    @Override
    public long countAll() {
        return userQueryRepository.countAll();
    }
}
