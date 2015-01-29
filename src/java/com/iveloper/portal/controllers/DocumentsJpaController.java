/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.controllers;

import com.iveloper.portal.controllers.exceptions.NonexistentEntityException;
import com.iveloper.portal.controllers.exceptions.PreexistingEntityException;
import com.iveloper.portal.controllers.exceptions.RollbackFailureException;
import com.iveloper.portal.entities.Documents;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
public class DocumentsJpaController implements Serializable {

    public DocumentsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Documents documents) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            em.persist(documents);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDocuments(documents.getDocumentid()) != null) {
                throw new PreexistingEntityException("Documents " + documents + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Documents documents) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            documents = em.merge(documents);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = documents.getDocumentid();
                if (findDocuments(id) == null) {
                    throw new NonexistentEntityException("The documents with id " + id + " no longer exists.");
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
            Documents documents;
            try {
                documents = em.getReference(Documents.class, id);
                documents.getDocumentid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The documents with id " + id + " no longer exists.", enfe);
            }
            em.remove(documents);
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

    public List<Documents> findDocumentsEntities() {
        return findDocumentsEntities(true, -1, -1);
    }

    public List<Documents> findDocumentsEntities(int maxResults, int firstResult) {
        return findDocumentsEntities(false, maxResults, firstResult);
    }

    private List<Documents> findDocumentsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Documents.class));
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

    public Documents findDocuments(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Documents.class, id);
        } finally {
            em.close();
        }
    }

    public List<Documents> findDocumentsByCustomerId(String customerid) {

        Query query = getEntityManager().createNamedQuery("Documents.findByCustomerid", com.iveloper.portal.entities.Documents.class);
        query.setParameter("customerid", customerid);

        return query.getResultList();
    }
    
    public List<Documents> findNotDownloadedDocumentsByCustomerId(String customerid) {

        Query query = getEntityManager().createNamedQuery("Documents.findNotDownloadedByCustomerid", com.iveloper.portal.entities.Documents.class);
        query.setParameter("customerid", customerid);

        return query.getResultList();
    }

    public List<Documents> findByDateRangeByCustomerId(String customerid, Date startdate, Date enddate) {

        Query query = getEntityManager().createNamedQuery("Documents.findByDateRangeByCustomerid", com.iveloper.portal.entities.Documents.class);
        query.setParameter("customerid", customerid);
        query.setParameter("startdate", startdate);
        query.setParameter("enddate", enddate);

        return query.getResultList();
    }

    public int getDocumentsCountNotDownloadedByCustomerid(String customerid) {

        Query query = getEntityManager().createNamedQuery("Documents.findNotDownloadedByCustomerid", com.iveloper.portal.entities.Docinfo.class);
        query.setParameter("customerid", customerid);

        return query.getResultList().size();
    }

    public int markAllNotDownloadedAsDownloadedByCustomerid(String customerid) throws RollbackFailureException, Exception {

        int result = 0;
        try {
            utx.begin();
            Query query = getEntityManager().createNamedQuery("Documents.markAllNotDownloadedAsDownloadedByCustomerid");
            query.setParameter("customerid", customerid);

            result = query.executeUpdate();
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }

            throw ex;
        }
        return result;
    }
    public int sumUpTimesDownloaded(String customerid, String documentid) throws RollbackFailureException, Exception {

        int result = 0;
        try {
            utx.begin();
            Query query = getEntityManager().createNamedQuery("Documents.sumUpTimesDownloaded");
            query.setParameter("customerid", customerid);
            query.setParameter("documentid", documentid);

            result = query.executeUpdate();
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }

            throw ex;
        }
        return result;
    }

    public int getDocumentsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Documents> rt = cq.from(Documents.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
