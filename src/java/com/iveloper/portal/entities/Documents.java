/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "documents")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Documents.findAll", query = "SELECT d FROM Documents d"),
    @NamedQuery(name = "Documents.findByDocumentid", query = "SELECT d FROM Documents d WHERE d.documentid = :documentid"),
    @NamedQuery(name = "Documents.findByDatecreated", query = "SELECT d FROM Documents d WHERE d.datecreated = :datecreated"),
    @NamedQuery(name = "Documents.findByDocapprefcode", query = "SELECT d FROM Documents d WHERE d.docapprefcode = :docapprefcode"),
    @NamedQuery(name = "Documents.findByDocautorizacion", query = "SELECT d FROM Documents d WHERE d.docautorizacion = :docautorizacion"),
    @NamedQuery(name = "Documents.findByDocautorizaciondate", query = "SELECT d FROM Documents d WHERE d.docautorizaciondate = :docautorizaciondate"),
    @NamedQuery(name = "Documents.findByDocnum", query = "SELECT d FROM Documents d WHERE d.docnum = :docnum"),
    @NamedQuery(name = "Documents.findByDocstatus", query = "SELECT d FROM Documents d WHERE d.docstatus = :docstatus"),
    @NamedQuery(name = "Documents.findByDoctypecode", query = "SELECT d FROM Documents d WHERE d.doctypecode = :doctypecode"),
    @NamedQuery(name = "Documents.findByNotifyemail", query = "SELECT d FROM Documents d WHERE d.notifyemail = :notifyemail"),
    @NamedQuery(name = "Documents.findByNotifyname", query = "SELECT d FROM Documents d WHERE d.notifyname = :notifyname"),
    @NamedQuery(name = "Documents.findByStatuschanged", query = "SELECT d FROM Documents d WHERE d.statuschanged = :statuschanged"),
    @NamedQuery(name = "Documents.findByDateRangeByCustomerid", query = "SELECT d FROM Documents d INNER JOIN Docinfo i WHERE d.documentid = i.documentid AND d.datecreated BETWEEN :startdate AND :enddate AND i.customerid = :customerid"),
    @NamedQuery(name = "Documents.findByCustomerid", query = "SELECT d FROM Documents d INNER JOIN Docinfo i WHERE d.documentid = i.documentid AND d.docstatus = 'AUTORIZADO' AND i.customerid = :customerid"),
    @NamedQuery(name = "Documents.findNotDownloadedByCustomerid", query = "SELECT d FROM Documents d INNER JOIN Docinfo i WHERE d.documentid = i.documentid AND d.docstatus = 'AUTORIZADO' AND i.timesdownloaded = 0 AND i.customerid = :customerid"),
    @NamedQuery(name = "Documents.markAllNotDownloadedAsDownloadedByCustomerid", query = "UPDATE Docinfo i SET i.timesdownloaded = 1 WHERE i.timesdownloaded = 0 AND i.customerid = :customerid AND i.documentid IN  (SELECT d.documentid FROM Documents d WHERE d.docstatus = 'AUTORIZADO')"),
    @NamedQuery(name = "Documents.sumUpTimesDownloaded", query = "UPDATE Docinfo i SET i.timesdownloaded = i.timesdownloaded+1 WHERE i.customerid = :customerid AND i.documentid = :documentid")})

public class Documents implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "documentid")
    private String documentid;
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Size(max = 255)
    @Column(name = "docapprefcode")
    private String docapprefcode;
    @Size(max = 255)
    @Column(name = "docautorizacion")
    private String docautorizacion;
    @Column(name = "docautorizaciondate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date docautorizaciondate;
    @Size(max = 255)
    @Column(name = "docnum")
    private String docnum;
    @Lob
    @Column(name = "docref")
    private byte[] docref;
    @Size(max = 255)
    @Column(name = "docstatus")
    private String docstatus;
    @Size(max = 255)
    @Column(name = "doctypecode")
    private String doctypecode;
    @Lob
    @Column(name = "document")
    private byte[] document;
    @Size(max = 255)
    @Column(name = "notifyemail")
    private String notifyemail;
    @Size(max = 255)
    @Column(name = "notifyname")
    private String notifyname;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "reference")
    private String reference;
    @Column(name = "statuschanged")
    @Temporal(TemporalType.TIMESTAMP)
    private Date statuschanged;
    @JoinColumn(name = "lotid", referencedColumnName = "lotid")
    @ManyToOne
    private Lots lotid;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "documents")
    private Docinfo docinfo;

    public Documents() {
    }

    public Documents(String documentid) {
        this.documentid = documentid;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    public String getDocapprefcode() {
        return docapprefcode;
    }

    public void setDocapprefcode(String docapprefcode) {
        this.docapprefcode = docapprefcode;
    }

    public String getDocautorizacion() {
        return docautorizacion;
    }

    public void setDocautorizacion(String docautorizacion) {
        this.docautorizacion = docautorizacion;
    }

    public Date getDocautorizaciondate() {
        return docautorizaciondate;
    }

    public void setDocautorizaciondate(Date docautorizaciondate) {
        this.docautorizaciondate = docautorizaciondate;
    }

    public String getDocnum() {
        return docnum;
    }

    public void setDocnum(String docnum) {
        this.docnum = docnum;
    }

    public byte[] getDocref() {
        return docref;
    }

    public void setDocref(byte[] docref) {
        this.docref = docref;
    }

    public String getDocstatus() {
        return docstatus;
    }

    public void setDocstatus(String docstatus) {
        this.docstatus = docstatus;
    }

    public String getDoctypecode() {
        return doctypecode;
    }

    public void setDoctypecode(String doctypecode) {
        this.doctypecode = doctypecode;
    }

    public byte[] getDocument() {
        return document;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public String getNotifyemail() {
        return notifyemail;
    }

    public void setNotifyemail(String notifyemail) {
        this.notifyemail = notifyemail;
    }

    public String getNotifyname() {
        return notifyname;
    }

    public void setNotifyname(String notifyname) {
        this.notifyname = notifyname;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Date getStatuschanged() {
        return statuschanged;
    }

    public void setStatuschanged(Date statuschanged) {
        this.statuschanged = statuschanged;
    }

    public Lots getLotid() {
        return lotid;
    }

    public void setLotid(Lots lotid) {
        this.lotid = lotid;
    }

    public Docinfo getDocinfo() {
        return docinfo;
    }

    public void setDocinfo(Docinfo docinfo) {
        this.docinfo = docinfo;
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
        if (!(object instanceof Documents)) {
            return false;
        }
        Documents other = (Documents) object;
        if ((this.documentid == null && other.documentid != null) || (this.documentid != null && !this.documentid.equals(other.documentid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.portal.entities.Documents[ documentid=" + documentid + " ]";
    }

}
