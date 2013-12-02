/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.colims.repository.SearchParameterRepository;
import static org.apache.batik.svggen.font.table.Table.name;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("parameterRepository")
public class SearchParametersHibernateRepository extends GenericHibernateRepository<SearchParameterSettings, Long> implements SearchParameterRepository {

}
