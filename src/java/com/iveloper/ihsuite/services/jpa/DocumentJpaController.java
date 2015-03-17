/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.jpa;

import com.iveloper.ihsuite.services.entities.Document;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.iveloper.ihsuite.services.entities.Lot;
import com.iveloper.ihsuite.services.jpa.exceptions.NonexistentEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.PreexistingEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
public class DocumentJpaController implements Serializable {

    public DocumentJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Document document) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {

            em = getEntityManager();
            em.getTransaction().begin();
            Lot lotId = document.getLotId();
            if (lotId != null) {
                lotId = em.getReference(lotId.getClass(), lotId.getId());
                document.setLotId(lotId);
            }
            em.persist(document);
            if (lotId != null) {
                lotId.getDocumentCollection().add(document);
                lotId = em.merge(lotId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDocument(document.getId()) != null) {
                throw new PreexistingEntityException("Document " + document + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Document document) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();

            Document persistentDocument = em.find(Document.class, document.getId());
            Lot lotIdOld = persistentDocument.getLotId();
            Lot lotIdNew = document.getLotId();
            if (lotIdNew != null) {
                lotIdNew = em.getReference(lotIdNew.getClass(), lotIdNew.getId());
                document.setLotId(lotIdNew);
            }
            document = em.merge(document);
            if (lotIdOld != null && !lotIdOld.equals(lotIdNew)) {
                lotIdOld.getDocumentCollection().remove(document);
                lotIdOld = em.merge(lotIdOld);
            }
            if (lotIdNew != null && !lotIdNew.equals(lotIdOld)) {
                lotIdNew.getDocumentCollection().add(document);
                lotIdNew = em.merge(lotIdNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = document.getId();
                if (findDocument(id) == null) {
                    throw new NonexistentEntityException("The document with id " + id + " no longer exists.");
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

            Document document;
            try {
                document = em.getReference(Document.class, id);
                document.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The document with id " + id + " no longer exists.", enfe);
            }
            Lot lotId = document.getLotId();
            if (lotId != null) {
                lotId.getDocumentCollection().remove(document);
                lotId = em.merge(lotId);
            }
            em.remove(document);
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

    public List<Document> findDocumentEntities() {
        return findDocumentEntities(true, -1, -1);
    }

    public List<Document> findDocumentEntities(int maxResults, int firstResult) {
        return findDocumentEntities(false, maxResults, firstResult);
    }

    private List<Document> findDocumentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Document.class));
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

    public Document findDocument(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Document.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Document> rt = cq.from(Document.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    /*
     Aqui empiezan las funciones definidas especificamente para el portal de usuarios
     */
    public List<Document> findDocumentsByCustomerId(String customerid) {

        Query query = getEntityManager().createNamedQuery("Document.findByCustomerid", com.iveloper.ihsuite.services.entities.Document.class);
        query.setParameter("customerid", customerid);

        return query.getResultList();
    }

    public List<Document> findNotDownloadedDocumentsByCustomerId(String customerid) {

        Query query = getEntityManager().createNamedQuery("Document.findNotDownloadedByCustomerid", com.iveloper.ihsuite.services.entities.Document.class);
        query.setParameter("customerid", customerid);

        return query.getResultList();
    }

    public List<Document> findByDateRangeByCustomerId(String customerid, Date startdate, Date enddate) {

        Query query = getEntityManager().createNamedQuery("Document.findByDateRangeByCustomerid", com.iveloper.ihsuite.services.entities.Document.class);
        query.setParameter("customerid", customerid);
        query.setParameter("startdate", startdate);
        query.setParameter("enddate", enddate);

        return query.getResultList();
    }

    public int getDocumentsCountNotDownloadedByCustomerid(String customerid) {

        Query query = getEntityManager().createNamedQuery("Document.findNotDownloadedByCustomerid", com.iveloper.ihsuite.services.entities.Document.class);
        query.setParameter("customerid", customerid);

        return query.getResultList().size();
    }

    public int markAllNotDownloadedAsDownloadedByCustomerid(String customerid) throws RollbackFailureException, Exception {

        int result = 0;
        try {
            utx.begin();
            Query query = getEntityManager().createNamedQuery("Document.markAllNotDownloadedAsDownloadedByCustomerid");
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
            Query query = getEntityManager().createNamedQuery("Document.sumUpTimesDownloaded");
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
}
