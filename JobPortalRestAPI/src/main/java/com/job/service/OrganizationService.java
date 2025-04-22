package com.job.service;

import com.job.models.Organization;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrganizationService {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/job_portal";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Varsh@12";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    public List<Organization> getAllOrganizations() {
        List<Organization> organizations = new ArrayList<>();
        String query = "SELECT * FROM organizations";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Organization organization = new Organization();
                organization.setOrganizationId(resultSet.getInt("OrganizationID"));
                organization.setName(resultSet.getString("Name"));
                organization.setDescription(resultSet.getString("Description"));
                organization.setLocation(resultSet.getString("Location"));
                organization.setIndustry(resultSet.getString("Industry"));
                organization.setWebsite(resultSet.getString("Website"));
                organization.setContactEmail(resultSet.getString("ContactEmail"));
                organization.setContactPhone(resultSet.getString("ContactPhone"));

                organizations.add(organization);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching organizations", e);
        }
        return organizations;
    }

    public boolean createOrganization(Organization organization) {
        String query = "INSERT INTO organizations (Name, Description, Location, Industry, Website, ContactEmail, ContactPhone) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, organization.getName());
            statement.setString(2, organization.getDescription());
            statement.setString(3, organization.getLocation());
            statement.setString(4, organization.getIndustry());
            statement.setString(5, organization.getWebsite());
            statement.setString(6, organization.getContactEmail());
            statement.setString(7, organization.getContactPhone());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error creating organization", e);
        }
    }
}
