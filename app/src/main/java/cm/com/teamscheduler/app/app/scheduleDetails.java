package cm.com.teamscheduler.app.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Position;
import cm.com.teamscheduler.app.entity.Schedule;
import cm.com.teamscheduler.app.entity.ScheduleReport;
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by void on 26.08.16.
 */
public class scheduleDetails extends AppCompatActivity {

    Schedule schedule;
    Long dateClicked;

    public Long getHighestPosition(List<Position> positions)
    {
        ArrayList<Long> longs = new ArrayList<Long>();
        for (Position p: positions)
        {
            longs.add(p.getLevel());
        }
        Collections.sort(longs);

        System.out.println(longs.get(0) + "--------------------------------------------------------------------------------------------");
        return longs.get(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_selected_view);

        final Schedule schedule = (Schedule) getIntent().getSerializableExtra("schedule");
        final Long dateClicked = (Long) getIntent().getSerializableExtra("dateClicked");
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat vsdf = new SimpleDateFormat("yyyy-MM-dd");
        Button btn = (Button) findViewById(R.id.create_report_btn);
        User user = Auth.getInstance().getLoggedUser();

        TextView tv = (TextView) findViewById(R.id.schedule_title);
        tv.setText(schedule.getTitle().toString());
        TextView tv1 = (TextView) findViewById(R.id.schedule_description);
        tv1.setText(schedule.getDescription().toString());
        TextView tv2 = (TextView) findViewById(R.id.schedule_startdate);
        tv2.setText(sdf.format(schedule.getStartDate()));
        TextView tv3 = (TextView) findViewById(R.id.schedule_enddate);
        tv3.setText(sdf.format(schedule.getEndDate()));
        TextView tv4 = (TextView) findViewById(R.id.schedule_recurringtime);
        tv4.setText(schedule.getRecurringTime().toString() + " Days");
        TextView tv5 = (TextView) findViewById(R.id.schedule_assignedteam);
        tv5.setText(schedule.getAssignedTeam().getTeamname().toString());
        TextView tv6 = (TextView) findViewById(R.id.schedule_location);
        tv6.setText(schedule.getLocation().getName().toString());

        //Disabling Create Report button when it should not be available
        //Check if today is the day clicked
        try {
            if (dateClicked != vsdf.parse(vsdf.format(new Date())).getTime())
            {
                btn.setEnabled(false);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Check if user is in assigned team - SHOULD WORK BUT ITS NEVER CHECKED ANYWAY
        Boolean check = false;
        for (User u : schedule.getAssignedTeam().getUsers())
            if (u.getUsername().equals(user.getUsername()))
                check = true;

        if (check == false) {
            btn.setEnabled(false);
        }

        //Check if user is highest position in team - WORKS
        List<Position> teamPositions = new ArrayList<Position>();
        for (User u : schedule.getAssignedTeam().getUsers())
            teamPositions.add(u.getPosition());

        if (!user.getPosition().getLevel().equals(getHighestPosition(teamPositions)))
        {
            btn.setEnabled(false);
        }

        //Check if a report already exists for the current date - WORKS
        for (ScheduleReport sr : schedule.getReports())
        {
            try {
                if (vsdf.parse(vsdf.format(sr.getDate())).getTime() == (vsdf.parse(vsdf.format(new Date())).getTime()))
                {
                    btn.setEnabled(false);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //Check if a schedule exists for the current day - WORKS
        try {
            Boolean check2 = false;
            for (long i = vsdf.parse(vsdf.format(schedule.getStartDate())).getTime(); i <= vsdf.parse(vsdf.format(schedule.getEndDate())).getTime(); i = i + (schedule.getRecurringTime() * 86400000)) {
                if (i == vsdf.parse(vsdf.format(new Date())).getTime()) {
                    {
                        check2 = true;
                    }
                }
            }
            if (check2 == false)
            {
                btn.setEnabled(false);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void createReportButtonOnClick(View v)
    {
        Intent i = new Intent(scheduleDetails.this, scheduleCreateReport.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("schedule", (Schedule) getIntent().getSerializableExtra("schedule"));
        i.putExtras(bundle);
        startActivity(i);
    }
}
