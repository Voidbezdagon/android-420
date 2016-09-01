package cm.com.teamscheduler.app.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by void on 24.08.16.
 */
public class ScheduleActivity implements Serializable {

    private Long id;
    private String description;
    private Schedule schedule;
    private List<ScheduleActivityReport> scheduleActivityReports;

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

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<ScheduleActivityReport> getScheduleActivityReports() {
        return scheduleActivityReports;
    }

    public void setScheduleActivityReports(List<ScheduleActivityReport> scheduleActivityReports) {
        this.scheduleActivityReports = scheduleActivityReports;
    }
}
