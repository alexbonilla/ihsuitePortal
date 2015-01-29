/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.controllers;

import com.iveloper.portal.controllers.exceptions.NonexistentEntityException;
import com.iveloper.portal.controllers.exceptions.PreexistingEntityException;
import com.iveloper.portal.controllers.exceptions.RollbackFailureException;
import com.iveloper.portal.entities.Accounts;
import com.iveloper.portal.security.PasswordEncryptionService;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
public class AccountsJpaController implements Serializable {

    public AccountsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Accounts accounts) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            em.persist(accounts);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findAccounts(accounts.getUser()) != null) {
                throw new PreexistingEntityException("Accounts " + accounts + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Accounts accounts) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
//            em.getTransaction().begin();
            accounts = em.merge(accounts);
//            em.getTransaction().commit();
            utx.commit();

        } catch (SecurityException | IllegalStateException ex) {
            try {
                em.getTransaction().rollback();
            } catch (IllegalStateException | SecurityException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = accounts.getUser();
                if (findAccounts(id) == null) {
                    throw new NonexistentEntityException("The accounts with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Accounts accounts;
            try {
                accounts = em.getReference(Accounts.class, id);
                accounts.getUser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The accounts with id " + id + " no longer exists.", enfe);
            }
            em.remove(accounts);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Accounts> findAccountsEntities() {
        return findAccountsEntities(true, -1, -1);
    }

    public List<Accounts> findAccountsEntities(int maxResults, int firstResult) {
        return findAccountsEntities(false, maxResults, firstResult);
    }

    private List<Accounts> findAccountsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Accounts.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Accounts findAccounts(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Accounts.class, id);
        } finally {
            em.close();
        }
    }

    public boolean validateCredentials(String user, String pwd) {
        PasswordEncryptionService pes = new PasswordEncryptionService();
        Accounts validatingAccount = findAccounts(user);
        boolean validation = false;
        if (validatingAccount != null) {
            try {
                validation = pes.authenticate(pwd, validatingAccount.getPwd(), validatingAccount.getSalt());
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                Logger.getLogger(AccountsJpaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return validation;
    }

    public int getAccountsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Accounts> rt = cq.from(Accounts.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
