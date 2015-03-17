/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.beans;


import com.iveloper.ihsuite.services.entities.Document;
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
public class DocumentFacade extends AbstractFacade<Document> {

    @PersistenceContext(unitName = "ihsuitea44dbdbb4c0b43b4a0e8f48c33dced6bPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentFacade() {
        super(Document.class);
    }

    public List<Document> findByCustomerId(String customerid) {

        Query query = getEntityManager().createNamedQuery("Document.findByCustomerid",Document.class);
        query.setParameter("customerid", customerid);
        List<Document> list = query.getResultList();

        return list;
    }
}
