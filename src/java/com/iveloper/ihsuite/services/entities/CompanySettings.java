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
@Table(name = "company_settings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CompanySettings.findAll", query = "SELECT c FROM CompanySettings c"),
    @NamedQuery(name = "CompanySettings.findById", query = "SELECT c FROM CompanySettings c WHERE c.id = :id"),
    @NamedQuery(name = "CompanySettings.findByName", query = "SELECT c FROM CompanySettings c WHERE c.name = :name"),
    @NamedQuery(name = "CompanySettings.findByDateEntered", query = "SELECT c FROM CompanySettings c WHERE c.dateEntered = :dateEntered"),
    @NamedQuery(name = "CompanySettings.findByCommercialName", query = "SELECT c FROM CompanySettings c WHERE c.commercialName = :commercialName"),
    @NamedQuery(name = "CompanySettings.findByVat", query = "SELECT c FROM CompanySettings c WHERE c.vat = :vat"),
    @NamedQuery(name = "CompanySettings.findByVatAddress", query = "SELECT c FROM CompanySettings c WHERE c.vatAddress = :vatAddress"),
    @NamedQuery(name = "CompanySettings.findByTimeForNextProcess", query = "SELECT c FROM CompanySettings c WHERE c.timeForNextProcess = :timeForNextProcess"),
    @NamedQuery(name = "CompanySettings.findByTimeToWaitForContingency", query = "SELECT c FROM CompanySettings c WHERE c.timeToWaitForContingency = :timeToWaitForContingency"),
    @NamedQuery(name = "CompanySettings.findByCancelDocumentOnNoContingency", query = "SELECT c FROM CompanySettings c WHERE c.cancelDocumentOnNoContingency = :cancelDocumentOnNoContingency"),
    @NamedQuery(name = "CompanySettings.findByDateModified", query = "SELECT c FROM CompanySettings c WHERE c.dateModified = :dateModified"),
    @NamedQuery(name = "CompanySettings.findByTimeWaitForAuthorization", query = "SELECT c FROM CompanySettings c WHERE c.timeWaitForAuthorization = :timeWaitForAuthorization"),
    @NamedQuery(name = "CompanySettings.findByEnvironment", query = "SELECT c FROM CompanySettings c WHERE c.environment = :environment")})
public class CompanySettings implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 36)
    @Column(name = "id")
    private String id;
    @Size(max = 100)
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "logo")
    private byte[] logo;
    @Column(name = "date_entered")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEntered;
    @Size(max = 150)
    @Column(name = "commercial_name")
    private String commercialName;
    @Size(max = 13)
    @Column(name = "vat")
    private String vat;
    @Size(max = 255)
    @Column(name = "vat_address")
    private String vatAddress;
    @Column(name = "time_for_next_process")
    private Integer timeForNextProcess;
    @Column(name = "time_to_wait_for_contingency")
    private Integer timeToWaitForContingency;
    @Column(name = "cancel_document_on_no_contingency")
    private Boolean cancelDocumentOnNoContingency;
    @Size(max = 45)
    @Column(name = "date_modified")
    private String dateModified;
    @Column(name = "time_wait_for_authorization")
    private Integer timeWaitForAuthorization;
    @Column(name = "environment")
    private Character environment;

    public CompanySettings() {
    }

    public CompanySettings(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    public String getCommercialName() {
        return commercialName;
    }

    public void setCommercialName(String commercialName) {
        this.commercialName = commercialName;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getVatAddress() {
        return vatAddress;
    }

    public void setVatAddress(String vatAddress) {
        this.vatAddress = vatAddress;
    }

    public Integer getTimeForNextProcess() {
        return timeForNextProcess;
    }

    public void setTimeForNextProcess(Integer timeForNextProcess) {
        this.timeForNextProcess = timeForNextProcess;
    }

    public Integer getTimeToWaitForContingency() {
        return timeToWaitForContingency;
    }

    public void setTimeToWaitForContingency(Integer timeToWaitForContingency) {
        this.timeToWaitForContingency = timeToWaitForContingency;
    }

    public Boolean getCancelDocumentOnNoContingency() {
        return cancelDocumentOnNoContingency;
    }

    public void setCancelDocumentOnNoContingency(Boolean cancelDocumentOnNoContingency) {
        this.cancelDocumentOnNoContingency = cancelDocumentOnNoContingency;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public Integer getTimeWaitForAuthorization() {
        return timeWaitForAuthorization;
    }

    public void setTimeWaitForAuthorization(Integer timeWaitForAuthorization) {
        this.timeWaitForAuthorization = timeWaitForAuthorization;
    }

    public Character getEnvironment() {
        return environment;
    }

    public void setEnvironment(Character environment) {
        this.environment = environment;
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
        if (!(object instanceof CompanySettings)) {
            return false;
        }
        CompanySettings other = (CompanySettings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.ihsuite.services.entities.CompanySettings[ id=" + id + " ]";
    }
    
}
