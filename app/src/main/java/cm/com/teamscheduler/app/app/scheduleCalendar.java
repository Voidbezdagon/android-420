package cm.com.teamscheduler.app.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.FloatProperty;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Location;
import cm.com.teamscheduler.app.entity.Position;
import cm.com.teamscheduler.app.entity.Schedule;
import cm.com.teamscheduler.app.entity.ScheduleActivity;
import cm.com.teamscheduler.app.entity.ScheduleActivityReport;
import cm.com.teamscheduler.app.entity.ScheduleReport;
import cm.com.teamscheduler.app.entity.Team;
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by void on 22.08.16.
 */
public class scheduleCalendar extends AppCompatActivity {

    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    ImageView iv;
    TextView tv;
    //END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Event Calendar");


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
                        startActivity(new Intent(scheduleCalendar.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(scheduleCalendar.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(scheduleCalendar.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(scheduleCalendar.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(scheduleCalendar.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(scheduleCalendar.this, activity_team.class));
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
                startActivity(new Intent(scheduleCalendar.this, activity_edit_logged_user.class));
            }
        });

        //END OF MENU

        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/Schedule/getAll/";
        final ArrayList<Schedule> list= new ArrayList<Schedule>();
        final ArrayList<String> displayList= new ArrayList<String>();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat vsdf = new SimpleDateFormat("yyyy-MM-dd");

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++)
                            {
                                //Parsing Schedule from JSON to Entity
                                Schedule item = new Schedule();
                                item.setReports(new ArrayList<ScheduleReport>());
                                item.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                item.setTitle(response.getJSONObject(i).getString("title"));
                                item.setDescription(response.getJSONObject(i).getString("description"));
                                item.setStartDate(new Date(Long.parseLong(response.getJSONObject(i).getString("startDate"))));
                                item.setEndDate(new Date(Long.parseLong(response.getJSONObject(i).getString("endDate"))));
                                item.setRecurringTime(Long.parseLong(response.getJSONObject(i).getString("recurringTime")));
                                //Setting Location
                                    Location location = new Location();
                                    location.setId(Long.parseLong(response.getJSONObject(i).getJSONObject("location").getString("id")));
                                    location.setCity(response.getJSONObject(i).getJSONObject("location").getString("city"));
                                    location.setName(response.getJSONObject(i).getJSONObject("location").getString("name"));
                                    location.setDetails(response.getJSONObject(i).getJSONObject("location").getString("details"));
                                    location.setRegion(response.getJSONObject(i).getJSONObject("location").getString("region"));
                                    location.setStreet(response.getJSONObject(i).getJSONObject("location").getString("street"));
                                    location.setStreetNumber(Integer.parseInt(response.getJSONObject(i).getJSONObject("location").getString("streetNumber")));
                                    location.setZip(Integer.parseInt(response.getJSONObject(i).getJSONObject("location").getString("zip")));
                                    location.setLat(Float.parseFloat(response.getJSONObject(i).getJSONObject("location").getString("lat")));
                                    location.setLng(Float.parseFloat(response.getJSONObject(i).getJSONObject("location").getString("lng")));
                                item.setLocation(location);
                                //Setting Assigned Team
                                    Team team = new Team();
                                    team.setId(Long.parseLong(response.getJSONObject(i).getJSONObject("assignedTeam").getString("id")));
                                    team.setTeamname(response.getJSONObject(i).getJSONObject("assignedTeam").getString("teamname"));
                                    //Setting Users to Team
                                        ArrayList<User> users = new ArrayList<User>();
                                        for (int j = 0; j < response.getJSONObject(i).getJSONObject("assignedTeam").getJSONArray("users").length(); j++)
                                        {
                                            User user = new User();
                                            user.setId(Long.parseLong(response.getJSONObject(i).getJSONObject("assignedTeam").getJSONArray("users").getJSONObject(j).getString("id")));
                                            user.setAdmin(Boolean.parseBoolean(response.getJSONObject(i).getJSONObject("assignedTeam").getJSONArray("users").getJSONObject(j).getString("admin")));
                                                //Setting Position to User
                                                Position position = new Position();
                                                position.setId(Long.parseLong(response.getJSONObject(i).getJSONObject("assignedTeam").getJSONArray("users").getJSONObject(j).getJSONObject("position").getString("id")));
                                                position.setLevel(Long.parseLong(response.getJSONObject(i).getJSONObject("assignedTeam").getJSONArray("users").getJSONObject(j).getJSONObject("position").getString("level")));
                                                position.setName(response.getJSONObject(i).getJSONObject("assignedTeam").getJSONArray("users").getJSONObject(j).getJSONObject("position").getString("name"));
                                            user.setPosition(position);
                                            user.setUsername(response.getJSONObject(i).getJSONObject("assignedTeam").getJSONArray("users").getJSONObject(j).getString("username"));
                                            users.add(j, user);
                                        }
                                        team.setUsers(users);
                                        team.setId(Long.parseLong(response.getJSONObject(i).getJSONObject("assignedTeam").getString("id")));
                                        team.setTeamname(response.getJSONObject(i).getJSONObject("assignedTeam").getString("teamname"));
                                item.setAssignedTeam(team);
                                //Setting Reports
                                ArrayList<ScheduleReport> reports = new ArrayList<ScheduleReport>();
                                try {
                                    if (response.getJSONObject(i).getJSONArray("reports").length() > 0) {
                                        for (int k = 0; k < response.getJSONObject(i).getJSONArray("reports").length(); k++)
                                        {
                                            ScheduleReport rep = new ScheduleReport();
                                            rep.setId(Long.parseLong(response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getString("id")));
                                            rep.setDescription(response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getString("description"));
                                            rep.setDate(vsdf.parse(vsdf.format(new Date(Long.parseLong(response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getString("date"))))));
                                            //Setting ActivityReports to Report
                                                ArrayList<ScheduleActivityReport> sarList = new ArrayList<ScheduleActivityReport>();
                                                if (response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getJSONArray("scheduleActivityReports").length() > 0)
                                                {
                                                    for (int l = 0; l < response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getJSONArray("scheduleActivityReports").length(); l++)
                                                    {
                                                        ScheduleActivityReport sar = new ScheduleActivityReport();
                                                        sar.setId(Long.parseLong(response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getJSONArray("scheduleActivityReports").getJSONObject(l).getString("id")));
                                                        sar.setDate(sdf.parse(sdf.format(new Date(response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getJSONArray("scheduleActivityReports").getJSONObject(l).getLong("date")))));
                                                        sar.setFinished(Boolean.parseBoolean(response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getJSONArray("scheduleActivityReports").getJSONObject(l).getString("isFinished")));
                                                        sarList.add(l, sar);
                                                    }
                                                }
                                            rep.setActivityReports(sarList);
                                            reports.add(k, rep);
                                        }
                                    }
                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                                item.setReports(reports);
                                //Setting Activities
                                ArrayList<ScheduleActivity> activities = new ArrayList<ScheduleActivity>();
                                try {
                                    if (response.getJSONObject(i).getJSONArray("activities").length() > 0) {
                                        for (int k = 0; k < response.getJSONObject(i).getJSONArray("activities").length(); k++)
                                        {
                                            ScheduleActivity act = new ScheduleActivity();
                                            act.setId(Long.parseLong(response.getJSONObject(i).getJSONArray("activities").getJSONObject(k).getString("id")));
                                            act.setDescription(response.getJSONObject(i).getJSONArray("activities").getJSONObject(k).getString("description"));
                                            //Setting ActivityReports to Report
                                            ArrayList<ScheduleActivityReport> sarList = new ArrayList<ScheduleActivityReport>();
                                            if (response.getJSONObject(i).getJSONArray("activities").getJSONObject(k).getJSONArray("scheduleActivityReports").length() > 0) {
                                                for (int l = 0; l < response.getJSONObject(i).getJSONArray("activities").getJSONObject(k).getJSONArray("scheduleActivityReports").length(); l++) {
                                                    ScheduleActivityReport sar = new ScheduleActivityReport();
                                                    sar.setId(Long.parseLong(response.getJSONObject(i).getJSONArray("activities").getJSONObject(k).getJSONArray("scheduleActivityReports").getJSONObject(l).getString("id")));
                                                    sar.setDate(sdf.parse(sdf.format(new Date(response.getJSONObject(i).getJSONArray("activities").getJSONObject(k).getJSONArray("scheduleActivityReports").getJSONObject(l).getLong("date")))));
                                                    sar.setFinished(Boolean.parseBoolean(response.getJSONObject(i).getJSONArray("activities").getJSONObject(k).getJSONArray("scheduleActivityReports").getJSONObject(l).getString("isFinished")));
                                                    sarList.add(sar);
                                                }
                                            }
                                            act.setScheduleActivityReports(sarList);
                                            activities.add(act);
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                item.setActivities(activities);
                                //Adding Schedule to List
                                list.add(i,item);
                                displayList.add(i,item.getTitle() + " " + item.getDescription() + " " + sdf.format(item.getStartDate()) + " " + sdf.format(item.getEndDate()));
                                try {
                                    boolean check = false;
                                    for (long j = vsdf.parse(vsdf.format(item.getStartDate())).getTime(); j <= vsdf.parse(vsdf.format(item.getEndDate())).getTime(); j = j + (item.getRecurringTime() * 86400000)) {
                                        for (ScheduleReport r : item.getReports()) {
                                            if (r.getDate() != null) {
                                                if (r.getDate().getTime() == j) {
                                                    Event ev = new Event(Color.RED, j);
                                                    check = true;
                                                    compactCalendarView.addEvent(ev);
                                                }
                                            }
                                        }
                                        if (check == false)
                                        {
                                            if (vsdf.parse(vsdf.format(new Date())).getTime() == j) {
                                                compactCalendarView.setCurrentDayBackgroundColor(Color.parseColor("#1DAB61"));
                                            }
                                            Event ev = new Event(Color.GREEN, j);
                                            compactCalendarView.addEvent(ev);
                                        }
                                        check = false;
                                    }
                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter adapter = new ArrayAdapter(scheduleCalendar.this, R.layout.custom_text, displayList);
                        ListView listview = (ListView) findViewById(R.id.listNoob);
                        //listview.setAdapter(adapter);

                        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
                            @Override
                            public void onDayClick(Date dateClicked) {
                                ArrayList<Schedule> filteredList = new ArrayList<Schedule>();
                                for (Schedule s : list)
                                {
                                    try {
                                        for (long j = vsdf.parse(vsdf.format(s.getStartDate())).getTime(); j <= vsdf.parse(vsdf.format(s.getEndDate())).getTime(); j = j + (s.getRecurringTime() * 86400000))
                                        {
                                            if (vsdf.parse(vsdf.format(dateClicked)).getTime() == j)
                                            {
                                                filteredList.add(s);
                                            }
                                        }
                                    }
                                    catch (ParseException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                Intent i = new Intent(scheduleCalendar.this, scheduleDayList.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("schedules", filteredList);
                                try {
                                    bundle.putLong("dateClicked", vsdf.parse(vsdf.format(dateClicked)).getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                i.putExtras(bundle);
                                startActivity(i);
                                //List<Event> events = compactCalendarView.getEvents(dateClicked);
                            }

                            @Override
                            public void onMonthScroll(Date firstDayOfNewMonth) {

                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("da eba guza");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("access-key", Auth.getInstance().getLoggedUser().getAccesskey());
                return headers;
            }
        };


        AppController.getInstance().addToRequestQueue(req, tag_json_arry);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
