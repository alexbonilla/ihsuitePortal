package com.iveloper.portal.controllers;


import com.iveloper.ihsuite.services.entities.Document;
import com.iveloper.ihsuite.services.jpa.DocumentJpaController;
import com.iveloper.ihsuite.services.security.LoginBeanUtils;
import com.iveloper.portal.jsf.util.JsfUtil;
import com.iveloper.portal.jsf.util.JsfUtil.PersistAction;
import com.iveloper.portal.beans.DocumentFacade;


import java.io.IOException;
import java.io.OutputStream;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;

@ManagedBean(name = "documentsController")
@SessionScoped
public class DocumentsController implements Serializable {

    @EJB
    private com.iveloper.portal.beans.DocumentFacade ejbFacade;
    private List<Document> items = null;
    private Document selected;
    private DocumentJpaController documentsJpaController;
    private String entityid;
    private String customerid;
    private int countNotDownloadedByCustomer;
    private Date startDate;
    private Date endDate;

    public DocumentsController() {
        HttpSession session = LoginBeanUtils.getSession();
        entityid = (String) session.getAttribute("entityid");
        customerid = (String) session.getAttribute("customerid");
        if (entityid != null) {
            try {
                EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihsuite" + entityid + "PU");
                Context c = new InitialContext();
                UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");
                documentsJpaController = new DocumentJpaController(utx, emf);
                

            } catch (NamingException ex) {
                Logger.getLogger(DocumentsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void getDocumentXML() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String documentid = request.getParameter("documentid");
        if (documentid != null) {

            selected = documentsJpaController.findDocument(documentid);

            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            ec.responseReset();
            ec.setResponseContentType("application/zip");
            ec.setResponseHeader("Content-Disposition", "attachment; filename=documento.zip");
            try (OutputStream servletOutputStream = ec.getResponseOutputStream()) {

                try (ZipOutputStream zip = new ZipOutputStream(servletOutputStream)) {

                    zip.putNextEntry(new ZipEntry(selected.getDocNum()+ ".xml"));
                    zip.write(selected.getDocument(), 0, selected.getDocument().length);
                    zip.closeEntry();

                    zip.flush();
                    zip.close();
                }
                
                selected.setTimesDownloaded(selected.getTimesDownloaded()+ 1);
                selected.setLastDownload(new Date());
                documentsJpaController.edit(selected);
                fc.responseComplete();
            } catch (SecurityException | IllegalStateException ex) {
                Logger.getLogger(DocumentsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DocumentsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(DocumentsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void getNotDownloadedDocuments() {

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        List<Document> documents = documentsJpaController.findNotDownloadedDocumentsByCustomerId(customerid);
        Iterator<Document> documentsItr = documents.iterator();
        ec.responseReset();
        ec.setResponseContentType("application/zip");
        ec.setResponseHeader("Content-Disposition", "attachment; filename=documentos.zip");
        try (OutputStream servletOutputStream = ec.getResponseOutputStream()) {

            try (ZipOutputStream zip = new ZipOutputStream(servletOutputStream)) {
                while (documentsItr.hasNext()) {
                    Document currentDocument = documentsItr.next();
                    zip.putNextEntry(new ZipEntry(currentDocument.getDocNum() + ".xml"));
                    zip.write(currentDocument.getDocument(), 0, currentDocument.getDocument().length);
                    zip.closeEntry();
                }
                zip.flush();
                zip.close();
            }

            documentsJpaController.markAllNotDownloadedAsDownloadedByCustomerid(customerid);
            fc.responseComplete();
        } catch (SecurityException | IllegalStateException ex) {
            Logger.getLogger(DocumentsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DocumentsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DocumentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getCountNotDownloadedByCustomer() {
        setCountNotDownloadedByCustomer(documentsJpaController.getDocumentsCountNotDownloadedByCustomerid(customerid));
        return countNotDownloadedByCustomer;
    }

    public void setCountNotDownloadedByCustomer(int countNotDownloadedByCustomer) {
        this.countNotDownloadedByCustomer = countNotDownloadedByCustomer;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Document getSelected() {
        return selected;
    }

    public void setSelected(Document selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DocumentFacade getFacade() {
        return ejbFacade;
    }

    public Document prepareCreate() {
        selected = new Document();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DocumentsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DocumentsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DocumentsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Document> getItems() {
        if (items == null) {
            if (customerid != null) {
                items = documentsJpaController.findDocumentsByCustomerId(customerid);
            } else {
                items = documentsJpaController.findDocumentEntities();
            }

        }
        return items;
    }

    public List<Document> getItemsByDateRange() {

        if (customerid != null && startDate != null && endDate != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DATE, 1);            
            items = documentsJpaController.findByDateRangeByCustomerId(customerid, startDate, c.getTime());
            System.out.println("getItemsByDateRange Count: " + items.size());
        } else if (customerid != null ){
            items = documentsJpaController.findDocumentsByCustomerId(customerid);
        }

        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    documentsJpaController.edit(selected);
                } else {
                    documentsJpaController.destroy(selected.getId());
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Document> getItemsAvailableSelectMany() {
        return documentsJpaController.findDocumentEntities();
    }

    public List<Document> getItemsAvailableSelectOne() {
        return documentsJpaController.findDocumentEntities();
    }

    @FacesConverter(forClass = Document.class)
    public static class DocumentsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DocumentsController controller = (DocumentsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "documentsController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Document) {
                Document o = (Document) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Document.class.getName()});
                return null;
            }
        }

    }

}
