package cm.com.teamscheduler.app.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
 * Created by kostadin on 30.08.16.
 */
public class activity_schedule extends AppCompatActivity {
    // Tag used to cancel the request
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

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
        setContentView(R.layout.schedule_view);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Schedule List");

        //MENU & TOOLBAR
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,dLayout, toolbar, R.string.drawer_open, R.string.drawer_close );
        dLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.user_list_view:
                        startActivity(new Intent(activity_schedule.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_schedule.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_schedule.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_schedule.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_schedule.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_schedule.this, activity_team.class));
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
        //END OF MENU

        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/Schedule/getAll/";


        final ArrayList<Schedule> schedules= new ArrayList<Schedule>();
        final ArrayList<String> displayList= new ArrayList<String>();
        final ArrayList<ScheduleActivity> scheduleActivities = new ArrayList<ScheduleActivity>();

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat vsdf = new SimpleDateFormat("yyyy-MM-dd");

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++)
                            {
                                Schedule schedule = new Schedule();
                                Team team = new Team();
                                Location location = new Location();
                                schedule.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                schedule.setTitle(response.getJSONObject(i).getString("title"));
                                schedule.setDescription(response.getJSONObject(i).getString("description"));
                                schedule.setStartDate(new Date(Long.parseLong(response.getJSONObject(i).getString("startDate"))));
                                schedule.setEndDate(new Date(Long.parseLong(response.getJSONObject(i).getString("endDate"))));
                                schedule.setRecurringTime(Long.parseLong(response.getJSONObject(i).getString("recurringTime")));
                                {
                                    team.setId(response.getJSONObject(i).getJSONObject("assignedTeam").getLong("id"));
                                    team.setTeamname(response.getJSONObject(i).getJSONObject("assignedTeam").getString("teamname"));
                                    location.setId(response.getJSONObject(i).getJSONObject("location").getLong("id"));
                                    location.setName(response.getJSONObject(i).getJSONObject("location").getString("name"));
                                }
                                schedule.setAssignedTeam(team);
                                schedule.setLocation(location);
                                for(int j=0; j<response.getJSONObject(i).getJSONArray("activities").length();j++){
                                    ScheduleActivity scheduleActivity = new ScheduleActivity();
                                    scheduleActivity.setId(Long.parseLong(response.getJSONObject(i).getJSONArray("activities").getJSONObject(j).getString("id")));
                                    scheduleActivity.setDescription(response.getJSONObject(i).getJSONArray("activities").getJSONObject(j).getString("description"));
                                    scheduleActivities.add(j,scheduleActivity);
                                }
                                schedule.setActivities(scheduleActivities);
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
                                                    ScheduleActivity sa = new ScheduleActivity();
                                                    sar.setId(Long.parseLong(response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getJSONArray("scheduleActivityReports").getJSONObject(l).getString("id")));
                                                    sar.setDate(sdf.parse(sdf.format(new Date(response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getJSONArray("scheduleActivityReports").getJSONObject(l).getLong("date")))));
                                                    sar.setFinished(Boolean.parseBoolean(response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getJSONArray("scheduleActivityReports").getJSONObject(l).getString("isFinished")));
                                                    sa.setDescription(response.getJSONObject(i).getJSONArray("reports").getJSONObject(k).getJSONArray("scheduleActivityReports").getJSONObject(l).getJSONObject("scheduleActivity").getString("description"));
                                                    sar.setScheduleActivity(sa);
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
                                schedule.setReports(reports);


                                schedules.add(i,schedule);
                                displayList.add(i,schedule.getTitle());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_arry);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_text, displayList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 120;
                view.setLayoutParams(params);

                return view;
            }
        };
        ListView listview = (ListView) findViewById(R.id.schedule_list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                Intent i = new Intent(activity_schedule.this, activity_schedule_details.class);
                i.putExtra("key2", position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("key", schedules);
                i.putExtras(bundle);
                startActivity(i);
            }

        });


    }
    //DRAWER MENU
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    //OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.main_menu_item_1);
        item.setTitle("Add New Schedule");
        item = menu.findItem(R.id.main_menu_item_2);
        item.setVisible(false);
        item = menu.findItem(R.id.main_menu_item_3);
        item.setVisible(false);
        item = menu.findItem(R.id.main_menu_item_4);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }


    //OPTIONS MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_item_1: startActivity(new Intent(activity_schedule.this, activity_create_schedule.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
