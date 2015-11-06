/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.yek.todo2;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

/**
 *
 * @author sihai
 */
@Named(value = "itemService")
@RequestScoped
public class ItemService {

    private static final Logger LOG = Logger.getLogger(ItemService.class.getName());

    @Inject
    @NotNull
    javax.servlet.http.HttpServletRequest request;

    @PersistenceContext(unitName = "me.yek_Todo2_war_1.0PU")
    private EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;

    private Item item = new Item();

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    private Page page = new Page();

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void update() {
        LOG.info(getItem().toString());
        try {
            utx.begin();
            if ("update".equals(getMode())) {
                em.merge(getItem());
            } else {
                em.persist(getItem());
            }
            utx.commit();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new RuntimeException(ex);
        }
    }

    public String edit(Item i) {
        setItem(i);
        //request.setAttribute("mode", "update");
        mode = "update";
        return "edit";
    }
    
    public void delete_notwork(Item i)throws Exception{
        //not work because item now detch mananged em
        utx.begin();
        em.remove(i);
        utx.commit();
    }
    
    public void delete(int id) throws Exception{
        utx.begin();
        Item i=em.find(Item.class, id);
        em.remove(i);
        utx.commit();
    }

    public int getTotalPage() {
        Query query = em.createNativeQuery("select count(*) from Item");
        int count = Integer.parseInt(Objects.toString(query.getSingleResult(), null));
        int ret = (int) Math.ceil((float) count / page.getPageSize());
        LOG.info("totoalPage : " + ret);
        return ret;
    }

    public List<Item> getList() {
        TypedQuery q = em.createNamedQuery("Item.findAll", Item.class);//没有分页算法
        q.setFirstResult((page.getPageIndex() - 1) * page.getPageSize());
        q.setMaxResults(page.getPageSize());
        List<Item> ret = q.getResultList();
        return ret;
    }

    public void nextPage() {
        page.setPageIndex(page.getPageIndex() + 1);
    }

    public void prevPage() {
        page.setPageIndex(page.getPageIndex() - 1);
    }

}
