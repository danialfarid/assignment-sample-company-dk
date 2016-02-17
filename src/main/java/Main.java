import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistence");

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Department department1 = new Department();
        department1.setName("Q/A");
        department1.setCity("a");
        department1.setCountry("a");
        department1.setAddress("a");

        Department department2 = new Department();
        department2.setName("HR");
        department2.setCity("a");
        department2.setCountry("a");
        department2.setAddress("a");

        Employee employee1 = new Employee();
        employee1.setName("Jack");
        employee1.setDepartment(department1);

        Employee employee2 = new Employee();
        employee2.setName("Mary");
        employee2.setDepartment(department2);

        em.persist(department1);
        em.persist(department2);
        em.persist(employee1);
        em.persist(employee2);

        long employeeId1 = employee1.getId();
        long employeeId2 = employee2.getId();

        em.getTransaction().commit();

        em.getTransaction().begin();

        Employee dbEmployee1 =em.find(Employee.class, employeeId1);
        System.out.println("dbEmployee " + dbEmployee1);

        Employee dbEmployee2 =em.find(Employee.class, employeeId2);
        System.out.println("dbEmployee " + dbEmployee2);

        em.getTransaction().commit();

        em.close();
        emf.close();

        port(Integer.valueOf(System.getenv("PORT")));
        staticFileLocation("/public");

        get("/hello", (req, res) -> "Hello World");

        post("/company", (req, res) -> {
            ObjectMapper mapper = new ObjectMapper();
            Company company = mapper.readValue(req.body(), Company.class);
            LOG.info("creating company: " + company);
            if (company.getOwners() == null || company.getOwners().isEmpty()) {
                throw new IllegalArgumentException("At least one company owner is required.");
            }
            return new IdResponse(DB.get().createCompany(company));
        }, Main::toJson);

        get("/company", (req, res) -> {
            return DB.get().listCompanies();
        }, Main::toJson);

        get("/company/:id", (req, res) -> {
            String idStr = req.params(":id");
            long id;
            try {
                id = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("id is not valid");
            }
            return DB.get().getCompany(id);
        }, Main::toJson);

        get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine());

        after((req, res) -> {
            res.type("application/json");
        });
        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });
        exception(ConstraintViolationException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });
        exception(ValidationException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });
        exception(Exception.class, (e, req, res) -> {
            res.status(400);
            LOG.log(Level.SEVERE, "", e);
            res.body(toJson(new ResponseError(e.getClass().getName() + e.getMessage() + e.getCause())));
        });

        get("/db", (req, res) -> {
            Connection connection = null;
            Map<String, Object> attributes = new HashMap<>();
            try {
//        connection = DatabaseUrl.extract().getConnection();

                Statement stmt = connection.createStatement();
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
                stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
                ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

                ArrayList<String> output = new ArrayList<String>();
                while (rs.next()) {
                    output.add("Read from DB: " + rs.getTimestamp("tick"));
                }

                attributes.put("results", output);
                return new ModelAndView(attributes, "db.ftl");
            } catch (Exception e) {
                attributes.put("message", "There was an error: " + e);
                return new ModelAndView(attributes, "error.ftl");
            } finally {
                if (connection != null) try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }, new FreeMarkerEngine());

    }

}
