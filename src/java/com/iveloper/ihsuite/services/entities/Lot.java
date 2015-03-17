/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
    @NamedQuery(name = "Lot.findAll", query = "SELECT l FROM Lot l"),
    @NamedQuery(name = "Lot.findById", query = "SELECT l FROM Lot l WHERE l.id = :id"),
    @NamedQuery(name = "Lot.findByDateEntered", query = "SELECT l FROM Lot l WHERE l.dateEntered = :dateEntered"),
    @NamedQuery(name = "Lot.findByLotNumber", query = "SELECT l FROM Lot l WHERE l.lotNumber = :lotNumber"),
    @NamedQuery(name = "Lot.findByLotOpen", query = "SELECT l FROM Lot l WHERE l.lotOpen = :lotOpen"),
    @NamedQuery(name = "Lot.findCandidatesForProcessing", query = "SELECT l FROM Lot l WHERE l.processed = FALSE AND l.inProcess = FALSE"),
    @NamedQuery(name = "Lot.findByLotType", query = "SELECT l FROM Lot l WHERE l.lotType = :lotType")})
public class Lot implements Serializable {
    @Column(name = "in_process")
    private Boolean inProcess;
    @Column(name = "date_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;
    @Column(name = "processed")
    private Boolean processed;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 36)
    @Column(name = "id")
    private String id;
    @Column(name = "date_entered")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEntered;
    @Size(max = 36)
    @Column(name = "lot_number")
    private String lotNumber;
    @Column(name = "lot_open")
    private Boolean lotOpen;
    @Size(max = 255)
    @Column(name = "lot_type")
    private String lotType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lotId")
    private Collection<ProcessLog> processLogCollection;
    @OneToMany(mappedBy = "lotId")
    private Collection<Document> documentCollection;

    public Lot() {
    }

    public Lot(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public Boolean getLotOpen() {
        return lotOpen;
    }

    public void setLotOpen(Boolean lotOpen) {
        this.lotOpen = lotOpen;
    }

    public String getLotType() {
        return lotType;
    }

    public void setLotType(String lotType) {
        this.lotType = lotType;
    }

    @XmlTransient
    public Collection<ProcessLog> getProcessLogCollection() {
        return processLogCollection;
    }

    public void setProcessLogCollection(Collection<ProcessLog> processLogCollection) {
        this.processLogCollection = processLogCollection;
    }

    @XmlTransient
    public Collection<Document> getDocumentCollection() {
        return documentCollection;
    }

    public void setDocumentCollection(Collection<Document> documentCollection) {
        this.documentCollection = documentCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lot)) {
            return false;
        }
        Lot other = (Lot) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.ihsuite.services.entities.Lot[ id=" + id + " ]";
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public Boolean getInProcess() {
        return inProcess;
    }

    public void setInProcess(Boolean inProcess) {
        this.inProcess = inProcess;
    }
    
}
