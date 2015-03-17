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
import com.iveloper.ihsuite.services.entities.Account;
import com.iveloper.ihsuite.services.entities.Company;
import com.iveloper.ihsuite.services.jpa.exceptions.NonexistentEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.PreexistingEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
public class CompanyJpaController implements Serializable {

    public CompanyJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Company company) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (company.getAccountCollection() == null) {
            company.setAccountCollection(new ArrayList<Account>());
        }
        EntityManager em = null;
        try {

            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Account> attachedAccountCollection = new ArrayList<Account>();
            for (Account accountCollectionAccountToAttach : company.getAccountCollection()) {
                accountCollectionAccountToAttach = em.getReference(accountCollectionAccountToAttach.getClass(), accountCollectionAccountToAttach.getUser());
                attachedAccountCollection.add(accountCollectionAccountToAttach);
            }
            company.setAccountCollection(attachedAccountCollection);
            em.persist(company);
            for (Account accountCollectionAccount : company.getAccountCollection()) {
                Company oldEntityidOfAccountCollectionAccount = accountCollectionAccount.getEntityid();
                accountCollectionAccount.setEntityid(company);
                accountCollectionAccount = em.merge(accountCollectionAccount);
                if (oldEntityidOfAccountCollectionAccount != null) {
                    oldEntityidOfAccountCollectionAccount.getAccountCollection().remove(accountCollectionAccount);
                    oldEntityidOfAccountCollectionAccount = em.merge(oldEntityidOfAccountCollectionAccount);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCompany(company.getId()) != null) {
                throw new PreexistingEntityException("Company " + company + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Company company) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {

            em = getEntityManager();
            em.getTransaction().begin();
            Company persistentCompany = em.find(Company.class, company.getId());
            Collection<Account> accountCollectionOld = persistentCompany.getAccountCollection();
            Collection<Account> accountCollectionNew = company.getAccountCollection();
            Collection<Account> attachedAccountCollectionNew = new ArrayList<Account>();
            for (Account accountCollectionNewAccountToAttach : accountCollectionNew) {
                accountCollectionNewAccountToAttach = em.getReference(accountCollectionNewAccountToAttach.getClass(), accountCollectionNewAccountToAttach.getUser());
                attachedAccountCollectionNew.add(accountCollectionNewAccountToAttach);
            }
            accountCollectionNew = attachedAccountCollectionNew;
            company.setAccountCollection(accountCollectionNew);
            company = em.merge(company);
            for (Account accountCollectionOldAccount : accountCollectionOld) {
                if (!accountCollectionNew.contains(accountCollectionOldAccount)) {
                    accountCollectionOldAccount.setEntityid(null);
                    accountCollectionOldAccount = em.merge(accountCollectionOldAccount);
                }
            }
            for (Account accountCollectionNewAccount : accountCollectionNew) {
                if (!accountCollectionOld.contains(accountCollectionNewAccount)) {
                    Company oldEntityidOfAccountCollectionNewAccount = accountCollectionNewAccount.getEntityid();
                    accountCollectionNewAccount.setEntityid(company);
                    accountCollectionNewAccount = em.merge(accountCollectionNewAccount);
                    if (oldEntityidOfAccountCollectionNewAccount != null && !oldEntityidOfAccountCollectionNewAccount.equals(company)) {
                        oldEntityidOfAccountCollectionNewAccount.getAccountCollection().remove(accountCollectionNewAccount);
                        oldEntityidOfAccountCollectionNewAccount = em.merge(oldEntityidOfAccountCollectionNewAccount);
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
                String id = company.getId();
                if (findCompany(id) == null) {
                    throw new NonexistentEntityException("The company with id " + id + " no longer exists.");
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
            Company company;
            try {
                company = em.getReference(Company.class, id);
                company.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The company with id " + id + " no longer exists.", enfe);
            }
            Collection<Account> accountCollection = company.getAccountCollection();
            for (Account accountCollectionAccount : accountCollection) {
                accountCollectionAccount.setEntityid(null);
                accountCollectionAccount = em.merge(accountCollectionAccount);
            }
            em.remove(company);
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

    public List<Company> findCompanyEntities() {
        return findCompanyEntities(true, -1, -1);
    }

    public List<Company> findCompanyEntities(int maxResults, int firstResult) {
        return findCompanyEntities(false, maxResults, firstResult);
    }

    private List<Company> findCompanyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Company.class));
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

    public Company findCompany(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Company.class, id);
        } finally {
            em.close();
        }
    }

    public int getCompanyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Company> rt = cq.from(Company.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Company> findActiveCompanies() {
//        Query query = getEntityManager().createNamedQuery("Company.findByActive", com.iveloper.ihsuite.services.entities.Company.class);
        Query query = getEntityManager().createQuery("SELECT c FROM Company c WHERE c.active = :active", com.iveloper.ihsuite.services.entities.Company.class);
        query.setParameter("active", Boolean.TRUE);

        return query.getResultList();
    }
}
