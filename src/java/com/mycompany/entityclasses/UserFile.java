/*
 * Created by Jinwoo Yom on 2017.02.21  * 
 * Copyright © 2017 Jinwoo Yom. All rights reserved. * 
 */
package com.mycompany.entityclasses;

import com.mycompany.managers.Constants;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yom
 */
@Entity
@Table(name = "UserFile")
@XmlRootElement

@NamedQueries({
    @NamedQuery(name = "UserFile.findAll", query = "SELECT u FROM UserFile u")
    , @NamedQuery(name = "UserFile.findById", query = "SELECT u FROM UserFile u WHERE u.id = :id")
    , @NamedQuery(name = "UserFile.findUserFilesByUserId", query = "SELECT u FROM UserFile u WHERE u.userId.id = :userId")
    , @NamedQuery(name = "UserFile.findByFilename", query = "SELECT u FROM UserFile u WHERE u.filename = :filename")
})

public class UserFile implements Serializable {

    /*
    ========================================================
    Instance variables representing the attributes (columns)
    of the UserFile table in the CloudDriveDB database.
    ========================================================
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "filename")
    private String filename;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User userId;

    /*
    ===================================================================
    Class constructors for instantiating a UserFile entity object to
    represent a row in the UserFile table in the CloudDriveDB database.
    ===================================================================
     */
    public UserFile() {
    }

    public UserFile(Integer id) {
        this.id = id;
    }

    public UserFile(Integer id, String filename) {
        this.id = id;
        this.filename = filename;
    }

    public UserFile(String filename, User id) {
        this.filename = filename;
        userId = id;
    }

    /*
    ======================================================
    Getter and Setter methods for the attributes (columns)
    of the UserFile table in the CloudDriveDB database.
    ======================================================
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    /*
    ================
    Instance Methods
    ================
     */
    /**
     * @return Generates and returns a hash code value for the object with id
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Checks if the UserFile object identified by 'object' is the same as the UserFile object identified by 'id'
     *
     * @param object The UserFile object identified by 'object'
     * @return True if the UserFile 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserFile)) {
            return false;
        }
        UserFile other = (UserFile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return the String representation of a UserFile id
     */
    @Override
    public String toString() {
        return id.toString();
    }

    /*
    ===================================================
    The following method is added to the generated code
    ===================================================
     */
    public String getFilePath() {
        return Constants.FILES_ABSOLUTE_PATH + getFilename();
    }

}
