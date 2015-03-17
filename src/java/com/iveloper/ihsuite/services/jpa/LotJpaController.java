/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.jpa;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.iveloper.ihsuite.services.entities.ProcessLog;
import java.util.ArrayList;
import java.util.Collection;
import com.iveloper.ihsuite.services.entities.Document;
import com.iveloper.ihsuite.services.entities.Lot;
import com.iveloper.ihsuite.services.jpa.exceptions.IllegalOrphanException;
import com.iveloper.ihsuite.services.jpa.exceptions.NonexistentEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.PreexistingEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
public class LotJpaController implements Serializable {

    public LotJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Lot lot) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (lot.getProcessLogCollection() == null) {
            lot.setProcessLogCollection(new ArrayList<ProcessLog>());
        }
        if (lot.getDocumentCollection() == null) {
            lot.setDocumentCollection(new ArrayList<Document>());
        }
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<ProcessLog> attachedProcessLogCollection = new ArrayList<ProcessLog>();
            for (ProcessLog processLogCollectionProcessLogToAttach : lot.getProcessLogCollection()) {
                processLogCollectionProcessLogToAttach = em.getReference(processLogCollectionProcessLogToAttach.getClass(), processLogCollectionProcessLogToAttach.getId());
                attachedProcessLogCollection.add(processLogCollectionProcessLogToAttach);
            }
            lot.setProcessLogCollection(attachedProcessLogCollection);
            Collection<Document> attachedDocumentCollection = new ArrayList<Document>();
            for (Document documentCollectionDocumentToAttach : lot.getDocumentCollection()) {
                documentCollectionDocumentToAttach = em.getReference(documentCollectionDocumentToAttach.getClass(), documentCollectionDocumentToAttach.getId());
                attachedDocumentCollection.add(documentCollectionDocumentToAttach);
            }
            lot.setDocumentCollection(attachedDocumentCollection);
            em.persist(lot);
            for (ProcessLog processLogCollectionProcessLog : lot.getProcessLogCollection()) {
                Lot oldLotIdOfProcessLogCollectionProcessLog = processLogCollectionProcessLog.getLotId();
                processLogCollectionProcessLog.setLotId(lot);
                processLogCollectionProcessLog = em.merge(processLogCollectionProcessLog);
                if (oldLotIdOfProcessLogCollectionProcessLog != null) {
                    oldLotIdOfProcessLogCollectionProcessLog.getProcessLogCollection().remove(processLogCollectionProcessLog);
                    oldLotIdOfProcessLogCollectionProcessLog = em.merge(oldLotIdOfProcessLogCollectionProcessLog);
                }
            }
            for (Document documentCollectionDocument : lot.getDocumentCollection()) {
                Lot oldLotIdOfDocumentCollectionDocument = documentCollectionDocument.getLotId();
                documentCollectionDocument.setLotId(lot);
                documentCollectionDocument = em.merge(documentCollectionDocument);
                if (oldLotIdOfDocumentCollectionDocument != null) {
                    oldLotIdOfDocumentCollectionDocument.getDocumentCollection().remove(documentCollectionDocument);
                    oldLotIdOfDocumentCollectionDocument = em.merge(oldLotIdOfDocumentCollectionDocument);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findLot(lot.getId()) != null) {
                throw new PreexistingEntityException("Lot " + lot + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Lot lot) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            Lot persistentLot = em.find(Lot.class, lot.getId());
            Collection<ProcessLog> processLogCollectionOld = persistentLot.getProcessLogCollection();
            Collection<ProcessLog> processLogCollectionNew = lot.getProcessLogCollection();
            Collection<Document> documentCollectionOld = persistentLot.getDocumentCollection();
            Collection<Document> documentCollectionNew = lot.getDocumentCollection();
            List<String> illegalOrphanMessages = null;
            for (ProcessLog processLogCollectionOldProcessLog : processLogCollectionOld) {
                if (!processLogCollectionNew.contains(processLogCollectionOldProcessLog)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProcessLog " + processLogCollectionOldProcessLog + " since its lotId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<ProcessLog> attachedProcessLogCollectionNew = new ArrayList<ProcessLog>();
            for (ProcessLog processLogCollectionNewProcessLogToAttach : processLogCollectionNew) {
                processLogCollectionNewProcessLogToAttach = em.getReference(processLogCollectionNewProcessLogToAttach.getClass(), processLogCollectionNewProcessLogToAttach.getId());
                attachedProcessLogCollectionNew.add(processLogCollectionNewProcessLogToAttach);
            }
            processLogCollectionNew = attachedProcessLogCollectionNew;
            lot.setProcessLogCollection(processLogCollectionNew);
            Collection<Document> attachedDocumentCollectionNew = new ArrayList<Document>();
            for (Document documentCollectionNewDocumentToAttach : documentCollectionNew) {
                documentCollectionNewDocumentToAttach = em.getReference(documentCollectionNewDocumentToAttach.getClass(), documentCollectionNewDocumentToAttach.getId());
                attachedDocumentCollectionNew.add(documentCollectionNewDocumentToAttach);
            }
            documentCollectionNew = attachedDocumentCollectionNew;
            lot.setDocumentCollection(documentCollectionNew);
            lot = em.merge(lot);
            for (ProcessLog processLogCollectionNewProcessLog : processLogCollectionNew) {
                if (!processLogCollectionOld.contains(processLogCollectionNewProcessLog)) {
                    Lot oldLotIdOfProcessLogCollectionNewProcessLog = processLogCollectionNewProcessLog.getLotId();
                    processLogCollectionNewProcessLog.setLotId(lot);
                    processLogCollectionNewProcessLog = em.merge(processLogCollectionNewProcessLog);
                    if (oldLotIdOfProcessLogCollectionNewProcessLog != null && !oldLotIdOfProcessLogCollectionNewProcessLog.equals(lot)) {
                        oldLotIdOfProcessLogCollectionNewProcessLog.getProcessLogCollection().remove(processLogCollectionNewProcessLog);
                        oldLotIdOfProcessLogCollectionNewProcessLog = em.merge(oldLotIdOfProcessLogCollectionNewProcessLog);
                    }
                }
            }
            for (Document documentCollectionOldDocument : documentCollectionOld) {
                if (!documentCollectionNew.contains(documentCollectionOldDocument)) {
                    documentCollectionOldDocument.setLotId(null);
                    documentCollectionOldDocument = em.merge(documentCollectionOldDocument);
                }
            }
            for (Document documentCollectionNewDocument : documentCollectionNew) {
                if (!documentCollectionOld.contains(documentCollectionNewDocument)) {
                    Lot oldLotIdOfDocumentCollectionNewDocument = documentCollectionNewDocument.getLotId();
                    documentCollectionNewDocument.setLotId(lot);
                    documentCollectionNewDocument = em.merge(documentCollectionNewDocument);
                    if (oldLotIdOfDocumentCollectionNewDocument != null && !oldLotIdOfDocumentCollectionNewDocument.equals(lot)) {
                        oldLotIdOfDocumentCollectionNewDocument.getDocumentCollection().remove(documentCollectionNewDocument);
                        oldLotIdOfDocumentCollectionNewDocument = em.merge(oldLotIdOfDocumentCollectionNewDocument);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = lot.getId();
                if (findLot(id) == null) {
                    throw new NonexistentEntityException("The lot with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            Lot lot;
            try {
                lot = em.getReference(Lot.class, id);
                lot.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The lot with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<ProcessLog> processLogCollectionOrphanCheck = lot.getProcessLogCollection();
            for (ProcessLog processLogCollectionOrphanCheckProcessLog : processLogCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Lot (" + lot + ") cannot be destroyed since the ProcessLog " + processLogCollectionOrphanCheckProcessLog + " in its processLogCollection field has a non-nullable lotId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Document> documentCollection = lot.getDocumentCollection();
            for (Document documentCollectionDocument : documentCollection) {
                documentCollectionDocument.setLotId(null);
                documentCollectionDocument = em.merge(documentCollectionDocument);
            }
            em.remove(lot);
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

    public List<Lot> findLotEntities() {
        return findLotEntities(true, -1, -1);
    }

    public List<Lot> findLotEntities(int maxResults, int firstResult) {
        return findLotEntities(false, maxResults, firstResult);
    }

    private List<Lot> findLotEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Lot.class));
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

    public Lot findLot(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Lot.class, id);
        } finally {
            em.close();
        }
    }

    public int getLotCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Lot> rt = cq.from(Lot.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Lot> findCandidatesForProcessing() {
        Query query = getEntityManager().createNamedQuery("Lot.findCandidatesForProcessing", com.iveloper.ihsuite.services.entities.Lot.class);        

        return query.getResultList();
    }
    
}
