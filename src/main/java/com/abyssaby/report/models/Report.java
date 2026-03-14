package com.abyssaby.report.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Report {
    
    private String reportId;
    private String reporter;
    private String reported;
    private String reason;
    private String server;
    private String status;  // OPEN, CLOSED
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private String closedBy;
    private String closeReason;

    // Constructor
    public Report(String reportId, String reporter, String reported, String reason, String server) {
        this.reportId = reportId;
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
        this.server = server;
        this.status = "OPEN";
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public String getReportId() {
        return reportId;
    }

    public String getReporter() {
        return reporter;
    }

    public String getReported() {
        return reported;
    }

    public String getReason() {
        return reason;
    }

    public String getServer() {
        return server;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public String getClosedBy() {
        return closedBy;
    }

    public String getCloseReason() {
        return closeReason;
    }

    // Setters
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void close(String closedBy, String closeReason) {
        this.status = "CLOSED";
        this.closedAt = LocalDateTime.now();
        this.closedBy = closedBy;
        this.closeReason = closeReason;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId='" + reportId + '\'' +
                ", reporter='" + reporter + '\'' +
                ", reported='" + reported + '\'' +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
