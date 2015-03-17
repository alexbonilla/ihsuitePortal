/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.jpa;

import com.iveloper.ihsuite.services.entities.CompanySettings;
import com.iveloper.ihsuite.services.jpa.exceptions.NonexistentEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.PreexistingEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
public class CompanySettingsJpaController implements Serializable {

    public CompanySettingsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CompanySettings companySettings) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(companySettings);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCompanySettings(companySettings.getId()) != null) {
                throw new PreexistingEntityException("CompanySettings " + companySettings + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CompanySettings companySettings) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            companySettings = em.merge(companySettings);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = companySettings.getId();
                if (findCompanySettings(id) == null) {
                    throw new NonexistentEntityException("The companySettings with id " + id + " no longer exists.");
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
            
            em = getEntityManager();
            em.getTransaction().begin();
            CompanySettings companySettings;
            try {
                companySettings = em.getReference(CompanySettings.class, id);
                companySettings.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The companySettings with id " + id + " no longer exists.", enfe);
            }
            em.remove(companySettings);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
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

    public List<CompanySettings> findCompanySettingsEntities() {
        return findCompanySettingsEntities(true, -1, -1);
    }

    public List<CompanySettings> findCompanySettingsEntities(int maxResults, int firstResult) {
        return findCompanySettingsEntities(false, maxResults, firstResult);
    }

    private List<CompanySettings> findCompanySettingsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CompanySettings.class));
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

    public CompanySettings findCompanySettings(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CompanySettings.class, id);
        } finally {
            em.close();
        }
    }

    public int getCompanySettingsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CompanySettings> rt = cq.from(CompanySettings.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
