package com.compomics.colims.repository.hibernate;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslator;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

/**
 * This helper class returns Query instances with additional order by clauses because HQL doesn't support parameters
 * there.
 * <p/>
 * Created by Niels Hulstaert on 14/10/15.
 */
public class HqlQueryConvertor {

    private static final String ORDER_BY_CLAUSE_START = "order by";
    private static final ASTQueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();

    /**
     * Private constructor to prevent instantiation.
     */
    private HqlQueryConvertor() {
    }

    /**
     * This method gets a named query and adds order by clauses for the specified columns.
     *
     * @param session     the hibernate session instance
     * @param queryName   the named query name
     * @param columnNames the column names to sort on
     * @return the Query instance
     * @throws SQLException thrown in case of a invalid character in the query string
     */
    public static Query getQueryOrderedBy(Session session, String queryName, Map<String, SortDirection> columnNames) {

        StringBuilder sb = new StringBuilder();
        sb.append(ORDER_BY_CLAUSE_START);

        int limit = columnNames.size();
        int i = 0;
        for (String columnName : columnNames.keySet()) {
            sb.append(columnName);

            if (columnNames.get(columnName).equals(SortDirection.ASCENDING))
                sb.append(" ASC");
            else
                sb.append(" DESC");

            if (i != (limit - 1)) {
                sb.append(", \n");
            }
        }
//        Query query = session.createSQLQuery(convertToSql(session, queryName) + sb.toString());
        Query query = session.createSQLQuery(convertToSql(session, queryName));

        return query;
    }

    private static String convertToSql(Session session, String hqlQueryText) {
        final QueryTranslator translator = translatorFactory.
                createQueryTranslator(
                        hqlQueryText,
                        hqlQueryText,
                        Collections.EMPTY_MAP, (SessionFactoryImplementor) session.getSessionFactory(),
                        null
                );
        translator.compile(Collections.EMPTY_MAP, false);

        return translator.getSQLString();
    }

}
