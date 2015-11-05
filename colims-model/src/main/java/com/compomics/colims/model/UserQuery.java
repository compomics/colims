package com.compomics.colims.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.jasypt.util.password.BasicPasswordEncryptor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a user entity in the database.
 *
 * @author Niels Hulstaert
 */
//@Table(name = "user_query")
//@Entity
public class UserQuery extends AuditableDatabaseEntity {

    private static final long serialVersionUID = -4086933454695081685L;

    /**
     * The query String.
     */
    @Basic(optional = false)
    @Column(name = "query_string", nullable = false)
    private String queryString;
    /**
     * The first name.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a first name.")
    @Length(min = 3, max = 20, message = "First name must be between {min} and {max} characters.")
    @Column(name = "first_name", nullable = false)
    private Integer test;

}
