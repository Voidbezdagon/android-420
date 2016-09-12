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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    ImageView iv;
    TextView tv;
    //END
    Long scheduleId;
    Schedule schedule = new Schedule();



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
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        dLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.user_list_view:
                        startActivity(new Intent(activity_schedule_details.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_schedule_details.this, scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_schedule_details.this, activity_schedule.class));
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
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Bundle extras = getIntent().getExtras();
        int position = -1;

        if (getIntent().getSerializableExtra("key") != null)
        {
            ArrayList<Schedule> schedules = (ArrayList<Schedule>) getIntent().getSerializableExtra("key");
            position = extras.getInt("key2");
            //The key argument here must match that used in the other activity
            if (schedules != null) {
                scheduleId = schedules.get(position).getId();
                schedule = schedules.get(position);
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
                final Long teamId = schedules.get(position).getAssignedTeam().getId();
                tv.setOnClickListener(new TextView.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity_schedule_details.this, activity_team_details.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("teamId", teamId);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                tv.setText(schedules.get(position).getAssignedTeam().getTeamname());
                tv = (TextView) findViewById(R.id.schedule_location);
                final Long locationId = schedules.get(position).getLocation().getId();
                tv.setOnClickListener(new TextView.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity_schedule_details.this, activity_location_details.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("locationId", locationId);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                tv.setText(schedules.get(position).getLocation().getName());
            }
        }
        else
        {
            //Begin get schedule request
            String tag_json_obj = "json_obj_req";
            String url2 = "http://10.0.2.2:8080/content/api/Schedule/get/" + getIntent().getLongExtra("scheduleId", 0);
            System.out.println(url2);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url2, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                scheduleId = response.getLong("id");
                                TextView tv = (TextView) findViewById(R.id.schedule_id);
                                tv.setText(response.getString("id"));
                                tv = (TextView) findViewById(R.id.schedule_title);
                                tv.setText(response.getString("title"));
                                tv = (TextView) findViewById(R.id.schedule_description);
                                tv.setText(response.getString("description"));
                                tv = (TextView) findViewById(R.id.schedule_startdate);
                                tv.setText(sdf.format(new Date(response.getLong("startDate"))));
                                tv = (TextView) findViewById(R.id.schedule_enddate);
                                tv.setText(sdf.format(new Date(response.getLong("endDate"))));
                                tv = (TextView) findViewById(R.id.schedule_recurringtime);
                                tv.setText(response.getString("recurringTime"));
                                tv = (TextView) findViewById(R.id.schedule_assignedteam);
                                final Long teamId = response.getJSONObject("assignedTeam").getLong("id");
                                tv.setOnClickListener(new TextView.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(activity_schedule_details.this, activity_team_details.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putLong("teamId", teamId);
                                        i.putExtras(bundle);
                                        startActivity(i);
                                    }
                                });
                                tv.setText(response.getJSONObject("assignedTeam").getString("teamname"));
                                tv = (TextView) findViewById(R.id.schedule_location);
                                final Long locationId = response.getJSONObject("location").getLong("id");
                                tv.setOnClickListener(new TextView.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(activity_schedule_details.this, activity_location_details.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putLong("locationId", locationId);
                                        i.putExtras(bundle);
                                        startActivity(i);
                                    }
                                });
                                tv.setText(response.getJSONObject("location").getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
            //End get schedule request
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
                Intent i = new Intent(activity_schedule_details.this, activity_schedule_report.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("schedule", schedule);
                i.putExtras(bundle);
                startActivity(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
