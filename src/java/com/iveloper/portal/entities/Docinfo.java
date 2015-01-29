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
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
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
@Table(name = "docinfo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Docinfo.findAll", query = "SELECT d FROM Docinfo d"),
    @NamedQuery(name = "Docinfo.findByDocumentid", query = "SELECT d FROM Docinfo d WHERE d.documentid = :documentid"),
    @NamedQuery(name = "Docinfo.findByCustomerid", query = "SELECT d FROM Docinfo d WHERE d.customerid = :customerid"),
    @NamedQuery(name = "Docinfo.findByTimesdownloaded", query = "SELECT d FROM Docinfo d WHERE d.timesdownloaded = :timesdownloaded"),
    @NamedQuery(name = "Docinfo.findByLastdownload", query = "SELECT d FROM Docinfo d WHERE d.lastdownload = :lastdownload")})
public class Docinfo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "documentid")
    private String documentid;
    @Size(max = 13)
    @Column(name = "customerid")
    private String customerid;
    @Column(name = "timesdownloaded")
    private Integer timesdownloaded;
    @Column(name = "lastdownload")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastdownload;
    @JoinColumn(name = "documentid", referencedColumnName = "documentid", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Documents documents;

    public Docinfo() {
    }

    public Docinfo(String documentid) {
        this.documentid = documentid;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public Integer getTimesdownloaded() {
        return timesdownloaded;
    }

    public void setTimesdownloaded(Integer timesdownloaded) {
        this.timesdownloaded = timesdownloaded;
    }

    public Date getLastdownload() {
        return lastdownload;
    }

    public void setLastdownload(Date lastdownload) {
        this.lastdownload = lastdownload;
    }

    public Documents getDocuments() {
        return documents;
    }

    public void setDocuments(Documents documents) {
        this.documents = documents;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (documentid != null ? documentid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Docinfo)) {
            return false;
        }
        Docinfo other = (Docinfo) object;
        if ((this.documentid == null && other.documentid != null) || (this.documentid != null && !this.documentid.equals(other.documentid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.portal.entities.Docinfo[ documentid=" + documentid + " ]";
    }
    
}
