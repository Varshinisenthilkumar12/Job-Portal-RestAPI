package com.job.service;

import com.job.models.Application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ApplicationService {
    private static final String URL = "jdbc:mysql://localhost:3306/job_portal";
    private static final String USER = "root";
    private static final String PASSWORD = "Varsh@12";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    
    public boolean createApplication(Application application) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO applications (user_id, organizationId, cover_letter, application_date) VALUES (?, ?, ?, ?)")) {
            
            stmt.setInt(1, application.getUserId());
            stmt.setInt(2, application.getOrganizationId()); 
            stmt.setString(3, application.getCoverLetter());
            stmt.setTimestamp(4, application.getApplicationDate());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
