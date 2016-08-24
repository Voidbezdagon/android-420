package cm.com.teamscheduler.app.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by void on 24.08.16.
 */
public class Schedule implements Serializable {

    private Long id;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private Long recurringTime;
    private Team assignedTeam;
    private Location location;
    private List<ScheduleActivity> activities;
    private List<ScheduleReport> reports;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getRecurringTime() {
        return recurringTime;
    }

    public void setRecurringTime(Long recurringTime) {
        this.recurringTime = recurringTime;
    }

    public Team getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(Team assignedTeam) {
        this.assignedTeam = assignedTeam;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<ScheduleActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<ScheduleActivity> activities) {
        this.activities = activities;
    }

    public List<ScheduleReport> getReports() {
        return reports;
    }

    public void setReports(List<ScheduleReport> reports) {
        this.reports = reports;
    }
}
