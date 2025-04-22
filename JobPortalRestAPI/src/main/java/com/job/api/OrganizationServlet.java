package com.job.api;

import com.job.models.Organization;
import com.job.models.User;
import com.job.service.OrganizationService;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "OrganizationServlet", urlPatterns = {"/api/organizations"})
public class OrganizationServlet extends HttpServlet {
    private OrganizationService organizationService;

    @Override
    public void init() throws ServletException {
        try {
            organizationService = new OrganizationService();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize OrganizationService", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {

            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("loggedInUser") == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\": \"User not logged in\"}");
                return;
            }

            User loggedInUser = (User) session.getAttribute("loggedInUser");

            List<Organization> organizations = organizationService.getAllOrganizations();

            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Organization org : organizations) {
                JsonObjectBuilder obj = Json.createObjectBuilder()
                    .add("organizationId", org.getOrganizationId())
                    .add("name", org.getName())
                    .add("description", org.getDescription())
                    .add("location", org.getLocation())
                    .add("industry", org.getIndustry())
                    .add("website", org.getWebsite())
                    .add("contactEmail", org.getContactEmail())
                    .add("contactPhone", org.getContactPhone());
                arrayBuilder.add(obj);
            }

            out.print(arrayBuilder.build().toString());

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try (InputStream is = req.getInputStream();
             JsonReader reader = Json.createReader(is);
             PrintWriter out = resp.getWriter()) {

            JsonObject jsonObject = reader.readObject();

            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("loggedInUser") == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\": \"User not logged in\"}");
                return;
            }

            User loggedInUser = (User) session.getAttribute("loggedInUser");
            if (!"employer".equals(loggedInUser.getUserType())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"error\": \"Only employers can create organizations\"}");
                return;
            }

            Organization organization = new Organization();
            organization.setName(jsonObject.getString("name", null));
            organization.setDescription(jsonObject.getString("description", null));
            organization.setLocation(jsonObject.getString("location", null));
            organization.setIndustry(jsonObject.getString("industry", null));
            organization.setWebsite(jsonObject.getString("website", null));
            organization.setContactEmail(jsonObject.getString("contactEmail", null));
            organization.setContactPhone(jsonObject.getString("contactPhone", null));

            boolean success = organizationService.createOrganization(organization);

            if (success) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"message\": \"Organization created successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to create organization\"}");
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
