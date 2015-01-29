/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.beans;

import com.iveloper.portal.entities.Documents;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author alexbonilla
 */
@Stateless
public class DocumentsFacade extends AbstractFacade<Documents> {

    @PersistenceContext(unitName = "ihsuitea44dbdbb4c0b43b4a0e8f48c33dced6bPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentsFacade() {
        super(Documents.class);
    }

    public List<Documents> findByCustomerId(String customerid) {

        Query query = getEntityManager().createNamedQuery("Documents.findByCustomerid",Documents.class);
        query.setParameter("customerid", customerid);
        List<Documents> list = query.getResultList();

        return list;
    }
}
