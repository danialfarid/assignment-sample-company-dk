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

    public int createCompany(@Valid CompanyOld company) {
        return withTransaction(em -> {
            try {
                em.persist(company);
                for (OwnerOld owner : company.getOwners()) {
                    em.persist(owner);
                }
            } catch (Throwable t) {
                LOG.throwing("", "", t);
                LOG.info(t.getMessage());
            }
            return company.getId();
        });
    }

    public List<CompanyBase> listCompanies() {
        return withEM(em -> {
            List<CompanyBase> list = em.createQuery("select NEW CompanyOld(c.id, c.name) from CompanyOld as c", CompanyOld.class)
                    .getResultList().stream().map(c -> new CompanyBase(c.getId(), c.getName()))
                    .collect(Collectors.toList());
            LOG.info("list companies: " + list);
            return list;
        });
    }

    public CompanyOld getCompany(Long id) {
        return withEM(em -> em.find(CompanyOld.class, id));
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
                    }  catch (RollbackException e) {
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
