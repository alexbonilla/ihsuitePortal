/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.security;

import com.iveloper.portal.controllers.AccountsJpaController;
import com.iveloper.portal.controllers.exceptions.RollbackFailureException;
import com.iveloper.portal.entities.Accounts;
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

    public String loginProject() {
        String destinationPage = "error";
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihAccountsPU");
            Context c = new InitialContext();
            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");

            AccountsJpaController accountController = new AccountsJpaController(utx, emf);
            boolean result = accountController.validateCredentials(uname, password);
            if (result) {
                // get Http Session and store username
                HttpSession session = LoginBeanUtils.getSession();
                session.setAttribute("username", uname);

                Accounts thisAccount = accountController.findAccounts(uname);
                //ask if this user is admin
                if (thisAccount.getRoles().equals("user")) {
                    thisAccount.setLastlogin(new Date());
                    thisAccount.setOnline(Short.valueOf("1"));
                    accountController.edit(thisAccount);

                    //Set entityid, to be able to use it to choose the correct PU for this account
                    session.setAttribute("entityid", thisAccount.getEntityid());
                    session.setAttribute("customerid", thisAccount.getUser());
                    this.entityid = thisAccount.getEntityid();
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

            AccountsJpaController accountController = new AccountsJpaController(utx, emf);
            Accounts thisAccount = accountController.findAccounts(uname);
            thisAccount.setOnline(Short.valueOf("0"));
            accountController.edit(thisAccount);

        } catch (NamingException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "/login";
    }
}
