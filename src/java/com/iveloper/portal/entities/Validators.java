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
@Table(name = "validators")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Validators.findAll", query = "SELECT v FROM Validators v"),
    @NamedQuery(name = "Validators.findByDocname", query = "SELECT v FROM Validators v WHERE v.docname = :docname"),
    @NamedQuery(name = "Validators.findByVersion", query = "SELECT v FROM Validators v WHERE v.version = :version"),
    @NamedQuery(name = "Validators.findByDatecreated", query = "SELECT v FROM Validators v WHERE v.datecreated = :datecreated")})
public class Validators implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "docname")
    private String docname;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "content")
    private byte[] content;
    @Size(max = 20)
    @Column(name = "version")
    private String version;
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;

    public Validators() {
    }

    public Validators(String docname) {
        this.docname = docname;
    }

    public Validators(String docname, byte[] content) {
        this.docname = docname;
        this.content = content;
    }

    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (docname != null ? docname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Validators)) {
            return false;
        }
        Validators other = (Validators) object;
        if ((this.docname == null && other.docname != null) || (this.docname != null && !this.docname.equals(other.docname))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.portal.entities.Validators[ docname=" + docname + " ]";
    }
    
}
