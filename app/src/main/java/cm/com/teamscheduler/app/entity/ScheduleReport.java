package cm.com.teamscheduler.app.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by void on 24.08.16.
 */
public class ScheduleReport implements Serializable {

    private Long id;
    private String description;
    private Date date;
    private Schedule schedule;
    private List<ScheduleActivityReport> activityReports;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<ScheduleActivityReport> getActivityReports() {
        return activityReports;
    }

    public void setActivityReports(List<ScheduleActivityReport> activityReports) {
        this.activityReports = activityReports;
    }
}

