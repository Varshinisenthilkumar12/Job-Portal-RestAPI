package com.job.models;
import java.sql.Timestamp;
public class Application {
    private int applicationId;
    private int userId;
    private int organizationId;  
    private int jobId;
    private String coverLetter;
    private Timestamp applicationDate;

    public Application(int userId, int organizationId, String coverLetter, Timestamp applicationDate) {
        this.userId = userId;
        this.organizationId = organizationId;
        this.jobId = jobId;
        this.coverLetter = coverLetter;
        this.applicationDate = applicationDate;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public Timestamp getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Timestamp applicationDate) {
        this.applicationDate = applicationDate;
    }
}
