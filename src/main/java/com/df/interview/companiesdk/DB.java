package com.df.interview.companiesdk;

import com.df.interview.companiesdk.model.Company;
import com.df.interview.companiesdk.model.CompanyName;
import com.df.interview.companiesdk.model.Owner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DB {
    private static Logger LOG = Logger.getLogger(DB.class.getName());
    private static final DB INSTANCE = new DB();
    EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistence");

    public Long updateCompany(Company company) {
        return withTransaction(em -> {
            company.getOwners().forEach(owner -> owner.setCompany(company));
            em.merge(company);
            company.getOwners().forEach(em::merge);
            return company.getId();
        });
    }

    public Long addOwner(long companyId, Owner owner) {
        Company company = getCompany(companyId);
        return withTransaction(em -> {
            company.getOwners().add(owner);
            owner.setCompany(company);
            em.persist(owner);
            em.merge(company);
            return owner.getId();
        });
    }

    public Long deleteOwner(long companyId, long ownerId) {
        Company company = getCompany(companyId);
        Owner owner = getOwner(ownerId);
        return withTransaction(em -> {
            company.getOwners().remove(owner);
            em.merge(company);
            em.remove(owner);
            return owner.getId();
        });
    }

    public Long createCompany(@Valid Company company) {
        return withTransaction(em -> {
            company.getOwners().forEach(owner -> owner.setCompany(company));
            em.persist(company);
            company.getOwners().forEach(em::persist);
            return company.getId();
        });
    }

    public List<CompanyName> listCompanies() {
        return withEM(em -> {
            List<CompanyName> list = em.createQuery("select NEW Company(c.id, c.name) from Company as c", Company.class)
                    .getResultList().stream().map(c -> new CompanyName(c.getId(), c.getName()))
                    .collect(Collectors.toList());
            LOG.info("list companies: " + list);
            return list;
        });
    }

    public Company getCompany(Long id) {
        return withEM(em -> em.find(Company.class, id));
    }

    public Owner getOwner(Long id) {
        return withEM(em -> em.find(Owner.class, id));
    }

    private <T> T withTransaction(Function<EntityManager, T> fn) {
        return withEM(em -> {
            try {
                em.getTransaction().begin();
                return fn.apply(em);
            } finally {
                if (em.getTransaction().isActive()) {
                    try {
                        em.getTransaction().commit();
                    } catch (RollbackException e) {
                        if (e.getCause() != null && e.getCause() instanceof ConstraintViolationException) {
                            throw new IllegalArgumentException(e.getCause());
                        } else {
                            throw e;
                        }
                    }
                }
            }
        });
    }

    private <T> T withEM(Function<EntityManager, T> fn) {
        EntityManager em = factory.createEntityManager();
        try {
            return fn.apply(em);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public static DB get() {
        return INSTANCE;
    }
}