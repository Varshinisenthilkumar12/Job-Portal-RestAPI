package com.job.api;

import com.job.models.Application;
import com.job.service.ApplicationService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.Instant;

@WebServlet(name = "JobServlet", urlPatterns = {"/api/job"})
public class JobServlet extends HttpServlet {

    private ApplicationService applicationService;

    @Override
    public void init() throws ServletException {
        applicationService = new ApplicationService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (JsonWriter writer = Json.createWriter(resp.getWriter())) {
                writer.writeObject(Json.createObjectBuilder()
                        .add("error", "User not logged in")
                        .build());
            }
            return;
        }

        Application application = parseApplicationFromJson(req);
        if (application == null) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (JsonWriter writer = Json.createWriter(resp.getWriter())) {
                writer.writeObject(Json.createObjectBuilder()
                        .add("error", "Invalid or missing fields")
                        .build());
            }
            return;
        }

        application.setApplicationDate(Timestamp.from(Instant.now()));

        boolean success = applicationService.createApplication(application);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("status", success ? "success" : "failure")
                .add("message", success ? "Application submitted successfully" : "Application submission failed")
                .build();

        resp.setStatus(success ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        try (JsonWriter writer = Json.createWriter(resp.getWriter())) {
            writer.writeObject(jsonResponse);
        }
    }

    private Application parseApplicationFromJson(HttpServletRequest req) {
        try (InputStream is = req.getInputStream();
             JsonReader reader = Json.createReader(is)) {

            JsonObject jsonObject = reader.readObject();

            int userId = jsonObject.getInt("userId", -1);
            int organizationId = jsonObject.getInt("organizationId", -1);
            String coverLetter = jsonObject.getString("coverLetter", "");

            if (userId == -1 || organizationId == -1) {
                return null;
            }

            return new Application(userId, organizationId, coverLetter, null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
