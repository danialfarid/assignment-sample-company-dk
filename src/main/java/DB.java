import sun.rmi.runtime.Log;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

public class DB {
    private static Logger LOG = Logger.getLogger(DB.class.getName());
    private static final DB INSTANCE = new DB();
    EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistence");

    public Long createCompany(Company company) {
        return withEM(em -> {
            try {
                em.persist(company);
            } catch (Throwable t) {
                LOG.throwing("", "", t);
                LOG.info(t.getMessage());
            }
            return company.getId();
        });
    }

    public List<CompanyBase> listCompanies() {
        return withEM(em -> {
            List<CompanyBase> list = em.createQuery("select NEW CompanyBase(c.id, c.name) from Company as c", CompanyBase.class).getResultList();
            LOG.info("list companies: " + list);
            return list;
        });
    }

    public Company getCompany(Long id) {
//        Session session = HibernateUtil.getSession();
//        Query query = session.createQuery("from Company as c where c.id=" + id);
//        List<Company> list = query.list();
//        return list.size() > 0 ? list.get(0) : null;
        return null;
    }

    private <T> T withEM(Function<EntityManager, T> fn) {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            return fn.apply(em);
        } finally {
            try {
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        }
    }

    public static DB get() {
        return INSTANCE;
    }
}
