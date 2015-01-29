/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.controllers;

import com.iveloper.portal.controllers.exceptions.NonexistentEntityException;
import com.iveloper.portal.controllers.exceptions.PreexistingEntityException;
import com.iveloper.portal.controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.iveloper.portal.entities.Documents;
import com.iveloper.portal.entities.Lots;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
public class LotsJpaController implements Serializable {

    public LotsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Lots lots) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (lots.getDocumentsList() == null) {
            lots.setDocumentsList(new ArrayList<Documents>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Documents> attachedDocumentsList = new ArrayList<Documents>();
            for (Documents documentsListDocumentsToAttach : lots.getDocumentsList()) {
                documentsListDocumentsToAttach = em.getReference(documentsListDocumentsToAttach.getClass(), documentsListDocumentsToAttach.getDocumentid());
                attachedDocumentsList.add(documentsListDocumentsToAttach);
            }
            lots.setDocumentsList(attachedDocumentsList);
            em.persist(lots);
            for (Documents documentsListDocuments : lots.getDocumentsList()) {
                Lots oldLotidOfDocumentsListDocuments = documentsListDocuments.getLotid();
                documentsListDocuments.setLotid(lots);
                documentsListDocuments = em.merge(documentsListDocuments);
                if (oldLotidOfDocumentsListDocuments != null) {
                    oldLotidOfDocumentsListDocuments.getDocumentsList().remove(documentsListDocuments);
                    oldLotidOfDocumentsListDocuments = em.merge(oldLotidOfDocumentsListDocuments);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findLots(lots.getLotid()) != null) {
                throw new PreexistingEntityException("Lots " + lots + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Lots lots) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Lots persistentLots = em.find(Lots.class, lots.getLotid());
            List<Documents> documentsListOld = persistentLots.getDocumentsList();
            List<Documents> documentsListNew = lots.getDocumentsList();
            List<Documents> attachedDocumentsListNew = new ArrayList<Documents>();
            for (Documents documentsListNewDocumentsToAttach : documentsListNew) {
                documentsListNewDocumentsToAttach = em.getReference(documentsListNewDocumentsToAttach.getClass(), documentsListNewDocumentsToAttach.getDocumentid());
                attachedDocumentsListNew.add(documentsListNewDocumentsToAttach);
            }
            documentsListNew = attachedDocumentsListNew;
            lots.setDocumentsList(documentsListNew);
            lots = em.merge(lots);
            for (Documents documentsListOldDocuments : documentsListOld) {
                if (!documentsListNew.contains(documentsListOldDocuments)) {
                    documentsListOldDocuments.setLotid(null);
                    documentsListOldDocuments = em.merge(documentsListOldDocuments);
                }
            }
            for (Documents documentsListNewDocuments : documentsListNew) {
                if (!documentsListOld.contains(documentsListNewDocuments)) {
                    Lots oldLotidOfDocumentsListNewDocuments = documentsListNewDocuments.getLotid();
                    documentsListNewDocuments.setLotid(lots);
                    documentsListNewDocuments = em.merge(documentsListNewDocuments);
                    if (oldLotidOfDocumentsListNewDocuments != null && !oldLotidOfDocumentsListNewDocuments.equals(lots)) {
                        oldLotidOfDocumentsListNewDocuments.getDocumentsList().remove(documentsListNewDocuments);
                        oldLotidOfDocumentsListNewDocuments = em.merge(oldLotidOfDocumentsListNewDocuments);
                    }
                }
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
                String id = lots.getLotid();
                if (findLots(id) == null) {
                    throw new NonexistentEntityException("The lots with id " + id + " no longer exists.");
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
            Lots lots;
            try {
                lots = em.getReference(Lots.class, id);
                lots.getLotid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The lots with id " + id + " no longer exists.", enfe);
            }
            List<Documents> documentsList = lots.getDocumentsList();
            for (Documents documentsListDocuments : documentsList) {
                documentsListDocuments.setLotid(null);
                documentsListDocuments = em.merge(documentsListDocuments);
            }
            em.remove(lots);
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

    public List<Lots> findLotsEntities() {
        return findLotsEntities(true, -1, -1);
    }

    public List<Lots> findLotsEntities(int maxResults, int firstResult) {
        return findLotsEntities(false, maxResults, firstResult);
    }

    private List<Lots> findLotsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Lots.class));
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

    public Lots findLots(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Lots.class, id);
        } finally {
            em.close();
        }
    }

    public int getLotsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Lots> rt = cq.from(Lots.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
