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
public class activity_team_details extends AppCompatActivity {

    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Long userId;
    //END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_details);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Team Profile");


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
                        startActivity(new Intent(activity_team_details.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_team_details.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_team_details.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_team_details.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_team_details.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_team_details.this, activity_team.class));
                        break;
                }

                return false;
            }
        });

        //END OF MENU
        Bundle extras = getIntent().getExtras();
        int position = -1;
        final ArrayList<String> displayUsers = new ArrayList<String>();
        final ArrayList<String> displaySchedules = new ArrayList<String>();
        ArrayList<Team> teams = (ArrayList<Team>) getIntent().getSerializableExtra("key");
        ArrayList<User> users;
        ArrayList<Schedule> schedules;
        position = extras.getInt("key2");
        //The key argument here must match that used in the other activity
        if(teams!=null) {
            userId = teams.get(position).getId();
            TextView tv = (TextView) findViewById(R.id.team_id);
            tv.setText(teams.get(position).getId().toString());
            tv = (TextView) findViewById(R.id.team_teamname);
            tv.setText(teams.get(position).getTeamname());
            users = (ArrayList<User>) teams.get(position).getUsers();
            for(User user : users){
                displayUsers.add(user.getFirstname() + " " + user.getLastname());
            }
            schedules = (ArrayList<Schedule>) teams.get(position).getSchedules();
            for(Schedule schedule : schedules){
                displaySchedules.add(schedule.getTitle());
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_text, displayUsers){
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
        ListView listview = (ListView) findViewById(R.id.team_users);
        listview.setAdapter(adapter);

        adapter = new ArrayAdapter(this, R.layout.custom_text, displaySchedules){
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
        listview = (ListView) findViewById(R.id.team_schedule);
        listview.setAdapter(adapter);
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
        item.setTitle("Edit Team");
        item = menu.findItem(R.id.main_menu_item_2);
        item.setTitle("Delete Team");
        item = menu.findItem(R.id.main_menu_item_3);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }


    //OPTIONS MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_item_1:
            {
                Intent i = new Intent(activity_team_details.this, activity_edit_user.class);
                Bundle bundle = new Bundle();
                bundle.putLong("userId", userId);
                i.putExtras(bundle);
                startActivity(i);
            }
            break;
            case R.id.main_menu_item_2:
            {
                String tag_json_obj = "json_obj_req";
                String url2 = "http://10.0.2.2:8080/content/api/User/delete/" + userId;
                System.out.println(url2);
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                        url2, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                startActivity(new Intent(activity_team_details.this, activity_user.class));
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
        }
        return super.onOptionsItemSelected(item);
    }
}