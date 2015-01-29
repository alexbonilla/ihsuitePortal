package com.iveloper.portal.controllers;

import com.iveloper.portal.entities.Documents;
import com.iveloper.portal.jsf.util.JsfUtil;
import com.iveloper.portal.jsf.util.JsfUtil.PersistAction;
import com.iveloper.portal.beans.DocumentsFacade;
import com.iveloper.portal.entities.Docinfo;
import com.iveloper.portal.security.LoginBeanUtils;
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
    private com.iveloper.portal.beans.DocumentsFacade ejbFacade;
    private List<Documents> items = null;
    private Documents selected;
    private DocumentsJpaController documentsJpaController;
    private DocinfoJpaController docinfoJpaController;
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
                documentsJpaController = new DocumentsJpaController(utx, emf);
                docinfoJpaController = new DocinfoJpaController(utx, emf);

            } catch (NamingException ex) {
                Logger.getLogger(DocumentsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void getDocumentXML() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String documentid = request.getParameter("documentid");
        if (documentid != null) {

            selected = documentsJpaController.findDocuments(documentid);

            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            ec.responseReset();
            ec.setResponseContentType("application/zip");
            ec.setResponseHeader("Content-Disposition", "attachment; filename=documento.zip");
            try (OutputStream servletOutputStream = ec.getResponseOutputStream()) {

                try (ZipOutputStream zip = new ZipOutputStream(servletOutputStream)) {

                    zip.putNextEntry(new ZipEntry(selected.getDocnum() + ".xml"));
                    zip.write(selected.getDocument(), 0, selected.getDocument().length);
                    zip.closeEntry();

                    zip.flush();
                    zip.close();
                }
                Docinfo thisDocInfo = selected.getDocinfo();
                thisDocInfo.setTimesdownloaded(thisDocInfo.getTimesdownloaded() + 1);
                thisDocInfo.setLastdownload(new Date());
                docinfoJpaController.edit(thisDocInfo);
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
        List<Documents> documents = documentsJpaController.findNotDownloadedDocumentsByCustomerId(customerid);
        Iterator<Documents> documentsItr = documents.iterator();
        ec.responseReset();
        ec.setResponseContentType("application/zip");
        ec.setResponseHeader("Content-Disposition", "attachment; filename=documentos.zip");
        try (OutputStream servletOutputStream = ec.getResponseOutputStream()) {

            try (ZipOutputStream zip = new ZipOutputStream(servletOutputStream)) {
                while (documentsItr.hasNext()) {
                    Documents currentDocument = documentsItr.next();
                    zip.putNextEntry(new ZipEntry(currentDocument.getDocnum() + ".xml"));
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

    public Documents getSelected() {
        return selected;
    }

    public void setSelected(Documents selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DocumentsFacade getFacade() {
        return ejbFacade;
    }

    public Documents prepareCreate() {
        selected = new Documents();
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

    public List<Documents> getItems() {
        if (items == null) {
            if (customerid != null) {
                items = documentsJpaController.findDocumentsByCustomerId(customerid);
            } else {
                items = documentsJpaController.findDocumentsEntities();
            }

        }
        return items;
    }

    public List<Documents> getItemsByDateRange() {

        if (customerid != null && startDate != null && endDate != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DATE, 1);            
            items = documentsJpaController.findByDateRangeByCustomerId(customerid, startDate, c.getTime());
            System.out.println("getItemsByDateRange Count: " + items.size());
        } else {
            items = documentsJpaController.findDocumentsEntities();
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
                    documentsJpaController.destroy(selected.getDocumentid());
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

    public List<Documents> getItemsAvailableSelectMany() {
        return documentsJpaController.findDocumentsEntities();
    }

    public List<Documents> getItemsAvailableSelectOne() {
        return documentsJpaController.findDocumentsEntities();
    }

    @FacesConverter(forClass = Documents.class)
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
            if (object instanceof Documents) {
                Documents o = (Documents) object;
                return getStringKey(o.getDocumentid());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Documents.class.getName()});
                return null;
            }
        }

    }

}
