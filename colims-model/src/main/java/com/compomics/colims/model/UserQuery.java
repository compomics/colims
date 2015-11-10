package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

/**
 * This class represents a user query entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "user_query")
@Entity
public class UserQuery extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 6364010853576853557L;

    /**
     * The query String.
     */
    @Basic(optional = false)
    @Length(max = 500, message = "Description must be less than {max} characters.")
    @Column(name = "query_string", nullable = false)
    private String queryString;
    /**
     * The first name.
     */
    @Basic(optional = true)
    @Column(name = "usage_count", nullable = true)
    private Integer usageCount;
    /**
     * The user that has executed this query.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "l_user_query_user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCOunt(Integer usageCOunt) {
        this.usageCount = usageCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserQuery userQuery = (UserQuery) o;

        return queryString.equals(userQuery.queryString);

    }

    @Override
    public int hashCode() {
        return queryString.hashCode();
    }
}
