/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.beans;

import com.iveloper.portal.entities.Docinfo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alexbonilla
 */
@Stateless
public class DocinfoFacade extends AbstractFacade<Docinfo> {
    @PersistenceContext(unitName = "ihsuitea44dbdbb4c0b43b4a0e8f48c33dced6bPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocinfoFacade() {
        super(Docinfo.class);
    }
    
}
