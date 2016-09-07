package cm.com.teamscheduler.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Schedule;
import cm.com.teamscheduler.app.entity.Team;
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by kostadin on 30.08.16.
 */
public class activity_schedule_details extends AppCompatActivity {
    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Long scheduleId;

    //END


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_details);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Schedule Details");


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
                        startActivity(new Intent(activity_schedule_details.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_schedule_details.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_schedule_details.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_schedule_details.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_schedule_details.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_schedule_details.this, activity_team.class));
                        break;
                }

                return false;
            }
        });

        //END OF MENU
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Bundle extras = getIntent().getExtras();
        int position = -1;
        final ArrayList<String> displayUsers = new ArrayList<String>();
        final ArrayList<String> displaySchedules = new ArrayList<String>();
        ArrayList<Schedule> schedules = (ArrayList<Schedule>) getIntent().getSerializableExtra("key");
        position = extras.getInt("key2");
        //The key argument here must match that used in the other activity
        if(schedules!=null) {
            scheduleId = schedules.get(position).getId();
            TextView tv = (TextView) findViewById(R.id.schedule_id);
            tv.setText(schedules.get(position).getId().toString());
            tv = (TextView) findViewById(R.id.schedule_title);
            tv.setText(schedules.get(position).getTitle());
            tv = (TextView) findViewById(R.id.schedule_description);
            tv.setText(schedules.get(position).getDescription());
            tv = (TextView) findViewById(R.id.schedule_startdate);
            tv.setText(sdf.format(schedules.get(position).getStartDate()));
            tv = (TextView) findViewById(R.id.schedule_enddate);
            tv.setText(sdf.format(schedules.get(position).getEndDate()));
            tv = (TextView) findViewById(R.id.schedule_recurringtime);
            tv.setText(schedules.get(position).getRecurringTime().toString());
            tv = (TextView) findViewById(R.id.schedule_assignedteam);
            tv.setText(schedules.get(position).getAssignedTeam().getTeamname());
            tv = (TextView) findViewById(R.id.schedule_location);
            tv.setText(schedules.get(position).getLocation().getName());
        }


    }

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
        item.setTitle("Edit Schedule");
        item = menu.findItem(R.id.main_menu_item_2);
        item.setTitle("Delete Schedule");
        item = menu.findItem(R.id.main_menu_item_3);
        item.setTitle("View Schedule Activities");
        item = menu.findItem(R.id.main_menu_item_4);
        item.setTitle("View Schedule Reports");
        return super.onCreateOptionsMenu(menu);
    }


    //OPTIONS MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_item_1:
            {
                Intent i = new Intent(activity_schedule_details.this, activity_edit_schedule.class);
                Bundle bundle = new Bundle();
                bundle.putLong("scheduleId", scheduleId);
                i.putExtras(bundle);
                startActivity(i);
            }
            break;
            case R.id.main_menu_item_2:
            {
                String tag_json_obj = "json_obj_req";
                String url2 = "http://10.0.2.2:8080/content/api/Schedule/delete/" + scheduleId;
                System.out.println(url2);
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                        url2, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                startActivity(new Intent(activity_schedule_details.this, activity_schedule.class));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("mazen hui");
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("access-key", Auth.getInstance().getLoggedUser().getAccesskey());
                        return headers;
                    }
                };
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
            }
            break;
            case R.id.main_menu_item_3: {
                Intent i = new Intent(activity_schedule_details.this, activity_schedule_activity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("scheduleId", scheduleId);
                i.putExtras(bundle);
                startActivity(i);
            }
            break;
            case R.id.main_menu_item_4:{
                startActivity(new Intent(activity_schedule_details.this,activity_schedule_report.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
