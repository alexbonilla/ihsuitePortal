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
import com.iveloper.ihsuite.services.entities.Lot;
import com.iveloper.ihsuite.services.entities.ProcessLog;
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
public class ProcessLogJpaController implements Serializable {

    public ProcessLogJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProcessLog processLog) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            Lot lotId = processLog.getLotId();
            if (lotId != null) {
                lotId = em.getReference(lotId.getClass(), lotId.getId());
                processLog.setLotId(lotId);
            }
            em.persist(processLog);
            if (lotId != null) {
                lotId.getProcessLogCollection().add(processLog);
                lotId = em.merge(lotId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findProcessLog(processLog.getId()) != null) {
                throw new PreexistingEntityException("ProcessLog " + processLog + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ProcessLog processLog) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            ProcessLog persistentProcessLog = em.find(ProcessLog.class, processLog.getId());
            Lot lotIdOld = persistentProcessLog.getLotId();
            Lot lotIdNew = processLog.getLotId();
            if (lotIdNew != null) {
                lotIdNew = em.getReference(lotIdNew.getClass(), lotIdNew.getId());
                processLog.setLotId(lotIdNew);
            }
            processLog = em.merge(processLog);
            if (lotIdOld != null && !lotIdOld.equals(lotIdNew)) {
                lotIdOld.getProcessLogCollection().remove(processLog);
                lotIdOld = em.merge(lotIdOld);
            }
            if (lotIdNew != null && !lotIdNew.equals(lotIdOld)) {
                lotIdNew.getProcessLogCollection().add(processLog);
                lotIdNew = em.merge(lotIdNew);
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
                String id = processLog.getId();
                if (findProcessLog(id) == null) {
                    throw new NonexistentEntityException("The processLog with id " + id + " no longer exists.");
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
            ProcessLog processLog;
            try {
                processLog = em.getReference(ProcessLog.class, id);
                processLog.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The processLog with id " + id + " no longer exists.", enfe);
            }
            Lot lotId = processLog.getLotId();
            if (lotId != null) {
                lotId.getProcessLogCollection().remove(processLog);
                lotId = em.merge(lotId);
            }
            em.remove(processLog);
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

    public List<ProcessLog> findProcessLogEntities() {
        return findProcessLogEntities(true, -1, -1);
    }

    public List<ProcessLog> findProcessLogEntities(int maxResults, int firstResult) {
        return findProcessLogEntities(false, maxResults, firstResult);
    }

    private List<ProcessLog> findProcessLogEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProcessLog.class));
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

    public ProcessLog findProcessLog(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ProcessLog.class, id);
        } finally {
            em.close();
        }
    }

    public int getProcessLogCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProcessLog> rt = cq.from(ProcessLog.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
