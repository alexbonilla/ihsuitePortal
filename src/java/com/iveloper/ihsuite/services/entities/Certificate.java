/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.entities;

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
@Table(name = "certificates")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Certificate.findAll", query = "SELECT c FROM Certificate c"),
    @NamedQuery(name = "Certificate.findByEntity", query = "SELECT c FROM Certificate c WHERE c.entity = :entity"),
    @NamedQuery(name = "Certificate.findByExpirationDate", query = "SELECT c FROM Certificate c WHERE c.expirationDate = :expirationDate"),
    @NamedQuery(name = "Certificate.findByName", query = "SELECT c FROM Certificate c WHERE c.name = :name"),
    @NamedQuery(name = "Certificate.findByPass", query = "SELECT c FROM Certificate c WHERE c.pass = :pass"),
    @NamedQuery(name = "Certificate.findByDateEntered", query = "SELECT c FROM Certificate c WHERE c.dateEntered = :dateEntered"),
    @NamedQuery(name = "Certificate.findByDateModified", query = "SELECT c FROM Certificate c WHERE c.dateModified = :dateModified")})
public class Certificate implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "entity")
    private String entity;
    @Lob
    @Column(name = "content")
    private byte[] content;
    @Column(name = "expiration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "pass")
    private String pass;
    @Column(name = "date_entered")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEntered;
    @Column(name = "date_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;

    public Certificate() {
    }

    public Certificate(String entity) {
        this.entity = entity;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (entity != null ? entity.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Certificate)) {
            return false;
        }
        Certificate other = (Certificate) object;
        if ((this.entity == null && other.entity != null) || (this.entity != null && !this.entity.equals(other.entity))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.ihsuite.services.entities.Certificate[ entity=" + entity + " ]";
    }
    
}
