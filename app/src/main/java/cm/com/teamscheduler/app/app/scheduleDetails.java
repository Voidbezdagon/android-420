package cm.com.teamscheduler.app.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
public class scheduleDetails extends AppCompatActivity implements OnMapReadyCallback {

    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    ImageView iv;
    TextView tv;
    //END

    //MAP
    private GoogleMap mMap;
    private Context context = this;
    private Float lat;
    private Float lng;

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

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Schedule Details");

        //MENU & TOOLBAR
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,dLayout, toolbar, R.string.drawer_open, R.string.drawer_close );
        dLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        if (Auth.getInstance().getLoggedUser().getAdmin() == false)
        {
            MenuItem item;
            item = (MenuItem) navigationView.getMenu().findItem(R.id.user_list_view);
            item.setVisible(false);
            item = (MenuItem) navigationView.getMenu().findItem(R.id.schedule_view);
            item.setVisible(false);
            item = (MenuItem) navigationView.getMenu().findItem(R.id.positions_view);
            item.setVisible(false);
            item = (MenuItem) navigationView.getMenu().findItem(R.id.location_view);
            item.setVisible(false);
            item = (MenuItem) navigationView.getMenu().findItem(R.id.team_view);
            item.setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.user_list_view:
                        startActivity(new Intent(scheduleDetails.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(scheduleDetails.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(scheduleDetails.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(scheduleDetails.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(scheduleDetails.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(scheduleDetails.this, activity_team.class));
                        break;
                }

                return false;
            }
        });
        byte[] decodedString = Base64.decode(Auth.getInstance().getLoggedUser().getAvatar(), Base64.DEFAULT);
        Bitmap pic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        View view = navigationView.inflateHeaderView(R.layout.navigarion_drawer_header);

        iv = (ImageView) view.findViewById(R.id.avatar);
        iv.setImageBitmap(pic);

        tv = (TextView) view.findViewById(R.id.header_name);
        tv.setText(Auth.getInstance().getLoggedUser().getUsername());

        tv = (TextView) view.findViewById(R.id.header_subname);
        tv.setText(Auth.getInstance().getLoggedUser().getFirstname() + " " + Auth.getInstance().getLoggedUser().getLastname());

        iv.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(scheduleDetails.this, activity_edit_logged_user.class));
            }
        });

        //END OF MENU

        final Schedule schedule = (Schedule) getIntent().getSerializableExtra("schedule");
        final Long dateClicked = (Long) getIntent().getSerializableExtra("dateClicked");
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat vsdf = new SimpleDateFormat("yyyy-MM-dd");
        Button btn = (Button) findViewById(R.id.create_report_btn);
        User user = Auth.getInstance().getLoggedUser();

        lat = schedule.getLocation().getLat();
        lng = schedule.getLocation().getLng();

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

    //MAP
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                // Add a marker in Sydney and move the camera
                LatLng cord = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(cord).title("Location"));
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cord, 17.0f));
            }
        }.start();
    }

    public void createReportButtonOnClick(View v)
    {
        Intent i = new Intent(scheduleDetails.this, scheduleCreateReport.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("schedule", (Schedule) getIntent().getSerializableExtra("schedule"));
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
