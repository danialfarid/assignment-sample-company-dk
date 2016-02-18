import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

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
        port(Integer.valueOf(System.getenv("PORT")));
        staticFileLocation("/public");
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Request-Method", "GET,POST,PUT,OPTIONS,DELETE");
            res.header("Access-Control-Allow-Headers", "Content-Type");
        });

        options("/*", (req, res) -> "OK");
        get("/hello", (req, res) -> "Hello World");

        post("/company", (req, res) -> {
            ObjectMapper mapper = new ObjectMapper();
            Company company = mapper.readValue(req.body(), Company.class);
            LOG.info("creating company: " + company);
            return new IdResponse(DB.get().createCompany(company));
        }, Main::toJson);

        put("/company/:companyId", (req, res) -> {
            ObjectMapper mapper = new ObjectMapper();
            Company company = mapper.readValue(req.body(), Company.class);
            LOG.info("updating company: " + company);
            return new IdResponse(DB.get().updateCompany(company));
        }, Main::toJson);

        post("/company/:companyId/owner", (req, res) -> {
            long companyId = toLong(req.params(":companyId"));
            ObjectMapper mapper = new ObjectMapper();
            Owner owner = mapper.readValue(req.body(), Owner.class);
            LOG.info("add owner to company: " + companyId + " " + owner);
            return new IdResponse(DB.get().addOwner(companyId, owner));
        }, Main::toJson);

        delete("/company/:companyId/owner/:ownerId", (req, res) -> {
            long companyId = toLong(req.params(":companyId"));
            long ownerId = toLong(req.params(":ownerId"));
            LOG.info("removing owner from company: " + companyId + " " + ownerId);
            return new IdResponse(DB.get().deleteOwner(companyId, ownerId));
        }, Main::toJson);

        get("/company", (req, res) -> {
            return DB.get().listCompanies();
        }, Main::toJson);

        get("/company/:companyId", (req, res) -> {
            return DB.get().getCompany(toLong(req.params(":companyId")));
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

    private static long toLong(String idStr) {
        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("id is not valid");
        }
        return id;
    }

}
