package com.job.api;

import com.job.models.User;
import com.job.service.UserService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "UserServlet", urlPatterns = {"/api/user/*"})
public class UserServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        
        if (path == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            return;
        }

        switch (path) {
            case "/login":
                handleLogin(req, resp);
                break;
            case "/register":
                handleRegister(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint: " + path);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User loginData = parseUserFromJson(req);

        if (loginData == null || loginData.getUsername() == null || loginData.getPassword() == null || loginData.getUserType() == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing login fields");
            return;
        }

        User user = userService.getUserByUsernameAndPassword(loginData.getUsername(), loginData.getPassword());

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        JsonObject jsonResponse;
        if (user != null && loginData.getUserType().equals(user.getUserType())) {

            // 🌟 Create session and store user object
            req.getSession(true).setAttribute("loggedInUser", user);

            jsonResponse = Json.createObjectBuilder()
                    .add("status", "success")
                    .add("userId", user.getId())
                    .add("username", user.getUsername())
                    .add("email", user.getEmail())
                    .add("userType", user.getUserType())
                    .add("name", user.getName())
                    .add("contactInfo", user.getContactInfo())
                    .build();

            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            jsonResponse = Json.createObjectBuilder()
                    .add("status", "failure")
                    .add("message", "Invalid username, password, or userType.")
                    .build();

            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        try (JsonWriter writer = Json.createWriter(resp.getWriter())) {
            writer.writeObject(jsonResponse);
        }
    }


    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User newUser = parseUserFromJson(req);

        if (newUser == null || newUser.getUsername() == null || newUser.getPassword() == null ||
            newUser.getEmail() == null || newUser.getUserType() == null ||
            newUser.getName() == null || newUser.getContactInfo() == null) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing registration fields");
            return;
        }

        boolean success = userService.createUser(newUser);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("status", success ? "success" : "failure")
                .add("message", success ? "User registered successfully" : "User registration failed")
                .build();

        resp.setStatus(success ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        try (JsonWriter writer = Json.createWriter(resp.getWriter())) {
            writer.writeObject(jsonResponse);
        }
    }

    private User parseUserFromJson(HttpServletRequest req) {
        try (InputStream is = req.getInputStream();
             JsonReader jsonReader = Json.createReader(is)) {

            JsonObject jsonObject = jsonReader.readObject();
            User user = new User();
            user.setUsername(jsonObject.getString("username", null));
            user.setPassword(jsonObject.getString("password", null));
            user.setEmail(jsonObject.getString("email", null));
            user.setUserType(jsonObject.getString("userType", null));
            user.setName(jsonObject.getString("name", null));
            user.setContactInfo(jsonObject.getString("contactInfo", null));

            return user;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
