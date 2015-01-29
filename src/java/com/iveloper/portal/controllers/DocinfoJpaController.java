/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.controllers;

import com.iveloper.portal.controllers.exceptions.IllegalOrphanException;
import com.iveloper.portal.controllers.exceptions.NonexistentEntityException;
import com.iveloper.portal.controllers.exceptions.PreexistingEntityException;
import com.iveloper.portal.controllers.exceptions.RollbackFailureException;
import com.iveloper.portal.entities.Docinfo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.iveloper.portal.entities.Documents;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
public class DocinfoJpaController implements Serializable {

    public DocinfoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Docinfo docinfo) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Documents documentsOrphanCheck = docinfo.getDocuments();
        if (documentsOrphanCheck != null) {
            Docinfo oldDocinfoOfDocuments = documentsOrphanCheck.getDocinfo();
            if (oldDocinfoOfDocuments != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Documents " + documentsOrphanCheck + " already has an item of type Docinfo whose documents column cannot be null. Please make another selection for the documents field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Documents documents = docinfo.getDocuments();
            if (documents != null) {
                documents = em.getReference(documents.getClass(), documents.getDocumentid());
                docinfo.setDocuments(documents);
            }
            em.persist(docinfo);
            if (documents != null) {
                documents.setDocinfo(docinfo);
                documents = em.merge(documents);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDocinfo(docinfo.getDocumentid()) != null) {
                throw new PreexistingEntityException("Docinfo " + docinfo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Docinfo docinfo) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Docinfo persistentDocinfo = em.find(Docinfo.class, docinfo.getDocumentid());
            Documents documentsOld = persistentDocinfo.getDocuments();
            Documents documentsNew = docinfo.getDocuments();
            List<String> illegalOrphanMessages = null;
            if (documentsNew != null && !documentsNew.equals(documentsOld)) {
                Docinfo oldDocinfoOfDocuments = documentsNew.getDocinfo();
                if (oldDocinfoOfDocuments != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Documents " + documentsNew + " already has an item of type Docinfo whose documents column cannot be null. Please make another selection for the documents field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (documentsNew != null) {
                documentsNew = em.getReference(documentsNew.getClass(), documentsNew.getDocumentid());
                docinfo.setDocuments(documentsNew);
            }
            docinfo = em.merge(docinfo);
            if (documentsOld != null && !documentsOld.equals(documentsNew)) {
                documentsOld.setDocinfo(null);
                documentsOld = em.merge(documentsOld);
            }
            if (documentsNew != null && !documentsNew.equals(documentsOld)) {
                documentsNew.setDocinfo(docinfo);
                documentsNew = em.merge(documentsNew);
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
                String id = docinfo.getDocumentid();
                if (findDocinfo(id) == null) {
                    throw new NonexistentEntityException("The docinfo with id " + id + " no longer exists.");
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
            Docinfo docinfo;
            try {
                docinfo = em.getReference(Docinfo.class, id);
                docinfo.getDocumentid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The docinfo with id " + id + " no longer exists.", enfe);
            }
            Documents documents = docinfo.getDocuments();
            if (documents != null) {
                documents.setDocinfo(null);
                documents = em.merge(documents);
            }
            em.remove(docinfo);
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

    public List<Docinfo> findDocinfoEntities() {
        return findDocinfoEntities(true, -1, -1);
    }

    public List<Docinfo> findDocinfoEntities(int maxResults, int firstResult) {
        return findDocinfoEntities(false, maxResults, firstResult);
    }

    private List<Docinfo> findDocinfoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Docinfo.class));
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

    public Docinfo findDocinfo(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Docinfo.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocinfoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Docinfo> rt = cq.from(Docinfo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    
    
}
