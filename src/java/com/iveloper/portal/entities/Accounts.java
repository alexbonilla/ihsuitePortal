/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alexbonilla
 */
@Entity
@Table(name = "accounts")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Accounts.findAll", query = "SELECT a FROM Accounts a"),
    @NamedQuery(name = "Accounts.findByUser", query = "SELECT a FROM Accounts a WHERE a.user = :user"),
    @NamedQuery(name = "Accounts.findByActive", query = "SELECT a FROM Accounts a WHERE a.active = :active"),
    @NamedQuery(name = "Accounts.findByDatecreated", query = "SELECT a FROM Accounts a WHERE a.datecreated = :datecreated"),
    @NamedQuery(name = "Accounts.findByEntityid", query = "SELECT a FROM Accounts a WHERE a.entityid = :entityid"),
    @NamedQuery(name = "Accounts.findByLastlogin", query = "SELECT a FROM Accounts a WHERE a.lastlogin = :lastlogin"),
    @NamedQuery(name = "Accounts.findByMail", query = "SELECT a FROM Accounts a WHERE a.mail = :mail"),
    @NamedQuery(name = "Accounts.findByOnline", query = "SELECT a FROM Accounts a WHERE a.online = :online"),
    @NamedQuery(name = "Accounts.findByRoles", query = "SELECT a FROM Accounts a WHERE a.roles = :roles")})
public class Accounts implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "user")
    private String user;
    @Column(name = "active")
    private Short active;
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Size(max = 255)
    @Column(name = "entityid")
    private String entityid;
    @Column(name = "lastlogin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastlogin;
    @Size(max = 255)
    @Column(name = "mail")
    private String mail;
    @Column(name = "online")
    private Short online;
    @Lob
    @Column(name = "pwd")
    private byte[] pwd;
    @Size(max = 255)
    @Column(name = "roles")
    private String roles;
    @Lob
    @Column(name = "salt")
    private byte[] salt;

    public Accounts() {
    }

    public Accounts(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Short getActive() {
        return active;
    }

    public void setActive(Short active) {
        this.active = active;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
    }

    public Date getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(Date lastlogin) {
        this.lastlogin = lastlogin;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Short getOnline() {
        return online;
    }

    public void setOnline(Short online) {
        this.online = online;
    }

    public byte[] getPwd() {
        return pwd;
    }

    public void setPwd(byte[] pwd) {
        this.pwd = pwd;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (user != null ? user.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Accounts)) {
            return false;
        }
        Accounts other = (Accounts) object;
        if ((this.user == null && other.user != null) || (this.user != null && !this.user.equals(other.user))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.portal.entities.Accounts[ user=" + user + " ]";
    }
    
}
