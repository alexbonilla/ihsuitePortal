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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "process_logs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProcessLog.findAll", query = "SELECT p FROM ProcessLog p"),
    @NamedQuery(name = "ProcessLog.findById", query = "SELECT p FROM ProcessLog p WHERE p.id = :id"),
    @NamedQuery(name = "ProcessLog.findByDateEntered", query = "SELECT p FROM ProcessLog p WHERE p.dateEntered = :dateEntered"),
    @NamedQuery(name = "ProcessLog.findByReference", query = "SELECT p FROM ProcessLog p WHERE p.reference = :reference"),
    @NamedQuery(name = "ProcessLog.findByType", query = "SELECT p FROM ProcessLog p WHERE p.type = :type"),
    @NamedQuery(name = "ProcessLog.findByCreatedBy", query = "SELECT p FROM ProcessLog p WHERE p.createdBy = :createdBy")})
public class ProcessLog implements Serializable {
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
    @Size(max = 255)
    @Column(name = "reference")
    private String reference;
    @Size(max = 20)
    @Column(name = "type")
    private String type;
    @Lob
    @Size(max = 65535)
    @Column(name = "message")
    private String message;
    @Size(max = 45)
    @Column(name = "created_by")
    private String createdBy;
    @JoinColumn(name = "lot_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Lot lotId;

    public ProcessLog() {
    }

    public ProcessLog(String id) {
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Lot getLotId() {
        return lotId;
    }

    public void setLotId(Lot lotId) {
        this.lotId = lotId;
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
        if (!(object instanceof ProcessLog)) {
            return false;
        }
        ProcessLog other = (ProcessLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.ihsuite.services.entities.ProcessLog[ id=" + id + " ]";
    }
    
}
