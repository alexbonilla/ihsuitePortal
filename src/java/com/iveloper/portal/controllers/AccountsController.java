package com.iveloper.portal.controllers;


import com.iveloper.ihsuite.services.entities.Account;
import com.iveloper.ihsuite.services.jpa.AccountJpaController;
import com.iveloper.ihsuite.services.security.LoginBeanUtils;
import com.iveloper.ihsuite.services.security.PasswordEncryptionService;
import com.iveloper.portal.controllers.util.JsfUtil;
import com.iveloper.portal.controllers.util.JsfUtil.PersistAction;
import com.iveloper.portal.beans.AccountFacade;


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

@ManagedBean(name = "accountsController")
@SessionScoped
public class AccountsController implements Serializable {

    @EJB
    private com.iveloper.portal.beans.AccountFacade ejbFacade;
    private List<Account> items = null;
    private Account selected;
    private String newpassword;

    public AccountsController() {
        try {
            HttpSession session = LoginBeanUtils.getSession();
            String customerid = (String) session.getAttribute("customerid");
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihAccountsPU");
            Context c = new InitialContext();
            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");
            AccountJpaController accountController = new AccountJpaController(utx, emf);

            selected = accountController.findAccount(customerid);

        } catch (NamingException ex) {
            Logger.getLogger(AccountsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Account getSelected() {
        return selected;
    }

    public void setSelected(Account selected) {
        this.selected = selected;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private AccountFacade getFacade() {
        return ejbFacade;
    }

    public Account prepareCreate() {
        selected = new Account();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AccountsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AccountsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("AccountsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Account> getItems() {
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
                    PasswordEncryptionService pes = new PasswordEncryptionService();
                    byte[] pwd = pes.getEncryptedPassword(newpassword, selected.getSalt());
                    selected.setPwd(pwd);
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

    public List<Account> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Account> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Account.class)
    public static class AccountsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AccountsController controller = (AccountsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "accountsController");
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
            if (object instanceof Account) {
                Account o = (Account) object;
                return getStringKey(o.getUser());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Account.class.getName()});
                return null;
            }
        }

    }

}
