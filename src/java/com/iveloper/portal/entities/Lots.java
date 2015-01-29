/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alexbonilla
 */
@Entity
@Table(name = "lots")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Lots.findAll", query = "SELECT l FROM Lots l"),
    @NamedQuery(name = "Lots.findByLotid", query = "SELECT l FROM Lots l WHERE l.lotid = :lotid"),
    @NamedQuery(name = "Lots.findByDatecreated", query = "SELECT l FROM Lots l WHERE l.datecreated = :datecreated"),
    @NamedQuery(name = "Lots.findByLotnumber", query = "SELECT l FROM Lots l WHERE l.lotnumber = :lotnumber"),
    @NamedQuery(name = "Lots.findByLotopen", query = "SELECT l FROM Lots l WHERE l.lotopen = :lotopen"),
    @NamedQuery(name = "Lots.findByLottype", query = "SELECT l FROM Lots l WHERE l.lottype = :lottype")})
public class Lots implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "lotid")
    private String lotid;
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Size(max = 255)
    @Column(name = "lotnumber")
    private String lotnumber;
    @Column(name = "lotopen")
    private Boolean lotopen;
    @Size(max = 255)
    @Column(name = "lottype")
    private String lottype;
    @OneToMany(mappedBy = "lotid")
    private List<Documents> documentsList;

    public Lots() {
    }

    public Lots(String lotid) {
        this.lotid = lotid;
    }

    public String getLotid() {
        return lotid;
    }

    public void setLotid(String lotid) {
        this.lotid = lotid;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    public String getLotnumber() {
        return lotnumber;
    }

    public void setLotnumber(String lotnumber) {
        this.lotnumber = lotnumber;
    }

    public Boolean getLotopen() {
        return lotopen;
    }

    public void setLotopen(Boolean lotopen) {
        this.lotopen = lotopen;
    }

    public String getLottype() {
        return lottype;
    }

    public void setLottype(String lottype) {
        this.lottype = lottype;
    }

    @XmlTransient
    public List<Documents> getDocumentsList() {
        return documentsList;
    }

    public void setDocumentsList(List<Documents> documentsList) {
        this.documentsList = documentsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lotid != null ? lotid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lots)) {
            return false;
        }
        Lots other = (Lots) object;
        if ((this.lotid == null && other.lotid != null) || (this.lotid != null && !this.lotid.equals(other.lotid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.portal.entities.Lots[ lotid=" + lotid + " ]";
    }
    
}
