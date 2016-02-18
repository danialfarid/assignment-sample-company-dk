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
            em.merge(company);
            company.getOwners().forEach(em::merge);
            return company.getId();
        });
    }

    public Long createCompany(@Valid Company company) {
        return withTransaction(em -> {
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
