/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.security;

import com.iveloper.ihsuite.services.entities.Account;
import com.iveloper.ihsuite.services.jpa.AccountJpaController;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;

@ManagedBean(name = "loginBean")
@SessionScoped

/**
 *
 * @author alexbonilla
 */
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String password;
    private String message, uname, entityid;
    private Account sessionAccount = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
    }

    public Account getSessionAccount() {
        return sessionAccount;
    }

    public void setSessionAccount(Account sessionAccount) {
        this.sessionAccount = sessionAccount;
    }

    public boolean verifyAccount() {
        boolean result = false;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihAccountsPU");
            Context c = new InitialContext();
            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");

            AccountJpaController accountController = new AccountJpaController(utx, emf);
            result = accountController.validateCredentials(uname, password);
            
            if (result) {

                Account thisAccount = accountController.findAccount(uname);
                //ask if this user is admin
                result = (thisAccount.getRoles().equals("admin") && thisAccount.getEntityid().getId().equals(entityid)); //if it is the correct role and entity

            }
        } catch (NamingException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public String login() {
        String destinationPage = "error";
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihAccountsPU");
            Context c = new InitialContext();
            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");

            AccountJpaController accountController = new AccountJpaController(utx, emf);
            boolean result = accountController.validateCredentials(uname, password);
            if (result) {
                // get Http Session and store username
                HttpSession session = LoginBeanUtils.getSession();
                session.setAttribute("username", uname);

                Account thisAccount = accountController.findAccount(uname);
                //ask if this user is admin
                if (thisAccount.getRoles().equals("user")) {
                    thisAccount.setLastlogin(new Date());
                    thisAccount.setOnline(Short.valueOf("1"));
                    accountController.edit(thisAccount);

                    //Set entityid, to be able to use it to choose the correct PU for this account                    
                    session.setAttribute("customerid", thisAccount.getUser());
                    if (thisAccount.getEntityid() != null) {
                        this.entityid = thisAccount.getEntityid().getId();
                        session.setAttribute("entityid", thisAccount.getEntityid().getId());
                    }
                    this.sessionAccount = thisAccount;
                    destinationPage = "documents/List";
                } else {
                    //not the correct role
                    FacesContext.getCurrentInstance().addMessage(
                            null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN,
                                    "Invalid Role!",
                                    "Please Try Again!"));
                    destinationPage = "login";
                }

            } else {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Invalid Login!",
                                "Please Try Again!"));

                // invalidate session, and redirect to other pages
                //message = "Invalid Login. Please Try Again!";
                destinationPage = "login";
            }
        } catch (NamingException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);

        } catch (RollbackFailureException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return destinationPage;
    }

    public String logout() {
        HttpSession session = LoginBeanUtils.getSession();
        session.invalidate();
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihAccountsPU");
            Context c = new InitialContext();
            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");

            AccountJpaController accountController = new AccountJpaController(utx, emf);
            Account thisAccount = accountController.findAccount(uname);
            thisAccount.setOnline(Short.valueOf("0"));
            accountController.edit(thisAccount);
            this.sessionAccount = null;

        } catch (NamingException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "/login";
    }
}
