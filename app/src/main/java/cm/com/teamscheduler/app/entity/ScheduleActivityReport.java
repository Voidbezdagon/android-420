package cm.com.teamscheduler.app.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by void on 24.08.16.
 */
public class ScheduleActivityReport implements Serializable {

    private Long id;
    private boolean isFinished;
    private Date date;
    private ScheduleActivity scheduleActivity;
    private ScheduleReport scheduleReport;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ScheduleActivity getScheduleActivity() {
        return scheduleActivity;
    }

    public void setScheduleActivity(ScheduleActivity scheduleActivity) {
        this.scheduleActivity = scheduleActivity;
    }

    public ScheduleReport getScheduleReport() {
        return scheduleReport;
    }

    public void setScheduleReport(ScheduleReport scheduleReport) {
        this.scheduleReport = scheduleReport;
    }
}
