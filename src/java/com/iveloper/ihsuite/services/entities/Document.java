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
@Table(name = "documents")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Document.findAll", query = "SELECT d FROM Document d"),
    @NamedQuery(name = "Document.findById", query = "SELECT d FROM Document d WHERE d.id = :id"),
    @NamedQuery(name = "Document.findByDateEntered", query = "SELECT d FROM Document d WHERE d.dateEntered = :dateEntered"),
    @NamedQuery(name = "Document.findByDocAppRefCode", query = "SELECT d FROM Document d WHERE d.docAppRefCode = :docAppRefCode"),
    @NamedQuery(name = "Document.findByDocAutorizacion", query = "SELECT d FROM Document d WHERE d.docAutorizacion = :docAutorizacion"),
    @NamedQuery(name = "Document.findByDocAutorizacionDate", query = "SELECT d FROM Document d WHERE d.docAutorizacionDate = :docAutorizacionDate"),
    @NamedQuery(name = "Document.findByDocNum", query = "SELECT d FROM Document d WHERE d.docNum = :docNum"),
    @NamedQuery(name = "Document.findByDocStatus", query = "SELECT d FROM Document d WHERE d.docStatus = :docStatus"),
    @NamedQuery(name = "Document.findByDocTypeCode", query = "SELECT d FROM Document d WHERE d.docTypeCode = :docTypeCode"),
    @NamedQuery(name = "Document.findByNotifyEmail", query = "SELECT d FROM Document d WHERE d.notifyEmail = :notifyEmail"),
    @NamedQuery(name = "Document.findByNotifyName", query = "SELECT d FROM Document d WHERE d.notifyName = :notifyName"),
    @NamedQuery(name = "Document.findByStatusChanged", query = "SELECT d FROM Document d WHERE d.statusChanged = :statusChanged"),
    @NamedQuery(name = "Document.findByCustomerId", query = "SELECT d FROM Document d WHERE d.customerId = :customerId"),
    @NamedQuery(name = "Document.findByTimesDownloaded", query = "SELECT d FROM Document d WHERE d.timesDownloaded = :timesDownloaded"),
    @NamedQuery(name = "Document.findByLastDownload", query = "SELECT d FROM Document d WHERE d.lastDownload = :lastDownload"),
    @NamedQuery(name = "Document.findBySentEmailId", query = "SELECT d FROM Document d WHERE d.sentEmailId = :sentEmailId"),
    //Aqui empiezan los queries definidos especificamente para el portal de usuarios
    @NamedQuery(name = "Document.findByDateRangeByCustomerid", query = "SELECT d FROM Document d WHERE d.dateEntered BETWEEN :startdate AND :enddate AND d.customerId = :customerid"),
    @NamedQuery(name = "Document.findByCustomerid", query = "SELECT d FROM Document d WHERE  d.docStatus = 'AUTORIZADO' AND d.customerId = :customerid"),
    @NamedQuery(name = "Document.findNotDownloadedByCustomerid", query = "SELECT d FROM Document d WHERE d.docStatus = 'AUTORIZADO' AND d.timesDownloaded = 0 AND d.customerId = :customerid"),
    @NamedQuery(name = "Document.markAllNotDownloadedAsDownloadedByCustomerid", query = "UPDATE Document d SET d.timesDownloaded = 1 WHERE d.timesDownloaded = 0 AND d.customerId = :customerid AND d.docStatus = 'AUTORIZADO'"),
    @NamedQuery(name = "Document.sumUpTimesDownloaded", query = "UPDATE Document d SET d.timesDownloaded = d.timesDownloaded+1 WHERE d.customerId = :customerid AND d.id = :documentid")})
public class Document implements Serializable {
    @Lob
    @Column(name = "doc_ref")
    private byte[] docRef;
    @Lob
    @Column(name = "document")
    private byte[] document;
    @Size(max = 49)
    @Column(name = "access_key")
    private String accessKey;
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
    @Column(name = "doc_app_ref_code")
    private String docAppRefCode;
    @Size(max = 255)
    @Column(name = "doc_autorizacion")
    private String docAutorizacion;
    @Column(name = "doc_autorizacion_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date docAutorizacionDate;
    @Size(max = 255)
    @Column(name = "doc_num")
    private String docNum;
    @Size(max = 255)
    @Column(name = "doc_status")
    private String docStatus;
    @Size(max = 255)
    @Column(name = "doc_type_code")
    private String docTypeCode;
    @Size(max = 255)
    @Column(name = "notify_email")
    private String notifyEmail;
    @Size(max = 255)
    @Column(name = "notify_name")
    private String notifyName;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "reference")
    private String reference;
    @Column(name = "status_changed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusChanged;
    @Size(max = 255)
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "times_downloaded")
    private Integer timesDownloaded;
    @Column(name = "last_download")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastDownload;
    @Size(max = 255)
    @Column(name = "sent_email_id")
    private String sentEmailId;
    @JoinColumn(name = "lot_id", referencedColumnName = "id")
    @ManyToOne
    private Lot lotId;

    public Document() {
    }

    public Document(String id) {
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

    public String getDocAppRefCode() {
        return docAppRefCode;
    }

    public void setDocAppRefCode(String docAppRefCode) {
        this.docAppRefCode = docAppRefCode;
    }

    public String getDocAutorizacion() {
        return docAutorizacion;
    }

    public void setDocAutorizacion(String docAutorizacion) {
        this.docAutorizacion = docAutorizacion;
    }

    public Date getDocAutorizacionDate() {
        return docAutorizacionDate;
    }

    public void setDocAutorizacionDate(Date docAutorizacionDate) {
        this.docAutorizacionDate = docAutorizacionDate;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public byte[] getDocRef() {
        return docRef;
    }

    public void setDocRef(byte[] docRef) {
        this.docRef = docRef;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }

    public String getDocTypeCode() {
        return docTypeCode;
    }

    public void setDocTypeCode(String docTypeCode) {
        this.docTypeCode = docTypeCode;
    }


    public String getNotifyEmail() {
        return notifyEmail;
    }

    public void setNotifyEmail(String notifyEmail) {
        this.notifyEmail = notifyEmail;
    }

    public String getNotifyName() {
        return notifyName;
    }

    public void setNotifyName(String notifyName) {
        this.notifyName = notifyName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Date getStatusChanged() {
        return statusChanged;
    }

    public void setStatusChanged(Date statusChanged) {
        this.statusChanged = statusChanged;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Integer getTimesDownloaded() {
        return timesDownloaded;
    }

    public void setTimesDownloaded(Integer timesDownloaded) {
        this.timesDownloaded = timesDownloaded;
    }

    public Date getLastDownload() {
        return lastDownload;
    }

    public void setLastDownload(Date lastDownload) {
        this.lastDownload = lastDownload;
    }

    public String getSentEmailId() {
        return sentEmailId;
    }

    public void setSentEmailId(String sentEmailId) {
        this.sentEmailId = sentEmailId;
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
        if (!(object instanceof Document)) {
            return false;
        }
        Document other = (Document) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.ihsuite.services.entities.Document[ id=" + id + " ]";
    }

    

    public byte[] getDocument() {
        return document;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
    
}
