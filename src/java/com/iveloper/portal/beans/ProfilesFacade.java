/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.portal.beans;

import com.iveloper.portal.entities.Profiles;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alexbonilla
 */
@Stateless
public class ProfilesFacade extends AbstractFacade<Profiles> {
    @PersistenceContext(unitName = "ihAccountsPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProfilesFacade() {
        super(Profiles.class);
    }
    
}
