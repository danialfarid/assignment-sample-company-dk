import org.hibernate.Query;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class DB {
    private static final DB INSTANCE = new DB();
    EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistence");

    public void createCompany(Company company) {
        EntityManager em = factory.createEntityManager();
        em.persist(company);
        em.close();
    }

    public List<Company> listCompanies() {
        EntityManager em = factory.createEntityManager();
        List<Company> list = em.createQuery("select c.name, c.id from Company as c", Company.class).getResultList();
        em.close();
        return list;
    }

    public Company getCompany(Long id) {
//        Session session = HibernateUtil.getSession();
//        Query query = session.createQuery("from Company as c where c.id=" + id);
//        List<Company> list = query.list();
//        return list.size() > 0 ? list.get(0) : null;
        return null;
    }

    public static DB get() {
        return INSTANCE;
    }
}
