package cm.com.teamscheduler.app.entity;

import java.io.Serializable;
import java.util.List;


/**
 * Created by void on 24.08.16.
 */
public class Team implements Serializable {

    private Long id;
    private String teamname;
    private List<User> users;
    private List<Schedule> schedules;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }


}
