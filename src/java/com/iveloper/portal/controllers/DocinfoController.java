package com.iveloper.portal.controllers;

import com.iveloper.portal.beans.DocinfoFacade;
import com.iveloper.portal.controllers.util.JsfUtil;
import com.iveloper.portal.controllers.util.JsfUtil.PersistAction;
import com.iveloper.portal.entities.Docinfo;
import com.iveloper.portal.security.LoginBeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;

@ManagedBean(name = "docinfoController")
@SessionScoped
public class DocinfoController implements Serializable {

    @EJB
    private com.iveloper.portal.beans.DocinfoFacade ejbFacade;
    private List<Docinfo> items = null;
    private Docinfo selected;
    private DocinfoJpaController docinfoJpaController;
    private String customerid;

    public DocinfoController() {
        HttpSession session = LoginBeanUtils.getSession();
        String entityid = (String) session.getAttribute("entityid");
        customerid = (String) session.getAttribute("customerid");
        if (entityid != null) {
            try {
                EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihsuiteAdmin" + entityid + "PU");
                Context c = new InitialContext();
                UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");
                docinfoJpaController = new DocinfoJpaController(utx, emf);
            } catch (NamingException ex) {
                Logger.getLogger(DocumentsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Docinfo getSelected() {
        return selected;
    }

    public void setSelected(Docinfo selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DocinfoFacade getFacade() {
        return ejbFacade;
    }

    public Docinfo prepareCreate() {
        selected = new Docinfo();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DocinfoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DocinfoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DocinfoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Docinfo> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
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

    public List<Docinfo> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Docinfo> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Docinfo.class)
    public static class DocinfoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DocinfoController controller = (DocinfoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "docinfoController");
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
            if (object instanceof Docinfo) {
                Docinfo o = (Docinfo) object;
                return getStringKey(o.getDocumentid());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Docinfo.class.getName()});
                return null;
            }
        }

    }

}
