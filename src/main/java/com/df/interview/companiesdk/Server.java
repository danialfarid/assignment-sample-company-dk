package com.df.interview.companiesdk;

import com.df.interview.companiesdk.model.Company;
import com.df.interview.companiesdk.model.Owner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static spark.Spark.*;

public class Server {
    private static final Logger LOG = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        port(Integer.valueOf(System.getenv("PORT")));
        staticFileLocation("/public");
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE");
            res.header("Access-Control-Allow-Headers", "Content-Type");
        });

        options("/*", (req, res) -> "OK");
        get("/hello", (req, res) -> "Hello World");

        post("/company", (req, res) -> {
            ObjectMapper mapper = new ObjectMapper();
            Company company = mapper.readValue(req.body(), Company.class);
            LOG.info("creating company: " + company);
            return new IdResponse(DB.get().createCompany(company));
        }, Server::toJson);

        put("/company/:companyId", (req, res) -> {
            ObjectMapper mapper = new ObjectMapper();
            Company company = mapper.readValue(req.body(), Company.class);
            LOG.info("updating company: " + company);
            return new IdResponse(DB.get().updateCompany(company));
        }, Server::toJson);

        post("/company/:companyId/owner", (req, res) -> {
            long companyId = toLong(req.params(":companyId"));
            ObjectMapper mapper = new ObjectMapper();
            Owner owner = mapper.readValue(req.body(), Owner.class);
            LOG.info("add owner to company: " + companyId + " " + owner);
            return new IdResponse(DB.get().addOwner(companyId, owner));
        }, Server::toJson);

        delete("/company/:companyId/owner/:ownerId", (req, res) -> {
            long companyId = toLong(req.params(":companyId"));
            long ownerId = toLong(req.params(":ownerId"));
            LOG.info("removing owner from company: " + companyId + " " + ownerId);
            return new IdResponse(DB.get().deleteOwner(companyId, ownerId));
        }, Server::toJson);

        get("/company", (req, res) -> {
            return DB.get().listCompanies();
        }, Server::toJson);

        get("/company/:companyId", (req, res) -> {
            return DB.get().getCompany(toLong(req.params(":companyId")));
        }, Server::toJson);

        get("/", (request, response) -> {
            response.redirect("index.html");
        });

        after((req, res) -> res.type("application/json"));

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });
        exception(ValidationException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });
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

    public static String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
