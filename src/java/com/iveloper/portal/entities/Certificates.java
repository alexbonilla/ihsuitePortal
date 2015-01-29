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
@Table(name = "certificates")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Certificates.findAll", query = "SELECT c FROM Certificates c"),
    @NamedQuery(name = "Certificates.findByEntity", query = "SELECT c FROM Certificates c WHERE c.entity = :entity"),
    @NamedQuery(name = "Certificates.findByName", query = "SELECT c FROM Certificates c WHERE c.name = :name"),
    @NamedQuery(name = "Certificates.findByPass", query = "SELECT c FROM Certificates c WHERE c.pass = :pass"),
    @NamedQuery(name = "Certificates.findByExpirationdate", query = "SELECT c FROM Certificates c WHERE c.expirationdate = :expirationdate")})
public class Certificates implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "entity")
    private String entity;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "content")
    private byte[] content;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "pass")
    private String pass;
    @Column(name = "expirationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationdate;

    public Certificates() {
    }

    public Certificates(String entity) {
        this.entity = entity;
    }

    public Certificates(String entity, String name, byte[] content, String pass) {
        this.entity = entity;
        this.name = name;
        this.content = content;
        this.pass = pass;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Date getExpirationdate() {
        return expirationdate;
    }

    public void setExpirationdate(Date expirationdate) {
        this.expirationdate = expirationdate;
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
        if (!(object instanceof Certificates)) {
            return false;
        }
        Certificates other = (Certificates) object;
        if ((this.entity == null && other.entity != null) || (this.entity != null && !this.entity.equals(other.entity))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.portal.entities.Certificates[ entity=" + entity + " ]";
    }
    
}
