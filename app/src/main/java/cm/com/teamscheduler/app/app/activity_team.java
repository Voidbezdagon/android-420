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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Position;
import cm.com.teamscheduler.app.entity.Schedule;
import cm.com.teamscheduler.app.entity.Team;
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by kostadin on 30.08.16.
 */
public class activity_team extends AppCompatActivity {
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
        setContentView(R.layout.team_view);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Team List");

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
                        startActivity(new Intent(activity_team.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_team.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_team.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_team.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_team.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_team.this, activity_team.class));
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
                startActivity(new Intent(activity_team.this, activity_edit_logged_user.class));
            }
        });
        //END OF MENU

        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/Team/getAll";

        final ArrayList<String> displayList= new ArrayList<String>();
        final ArrayList<Team> teams = new ArrayList<Team>();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++)
                            {
                                Team team = new Team();
                                team.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                team.setTeamname(response.getJSONObject(i).getString("teamname"));
                                ArrayList<User> users= new ArrayList<User>();
                                for (int j = 0; j < response.getJSONObject(i).getJSONArray("users").length(); j++)
                                {
                                    User user = new User();
                                    user.setId(Long.parseLong(response.getJSONObject(i).getJSONArray("users").getJSONObject(j).getString("id")));
                                    user.setAdmin(Boolean.parseBoolean(response.getJSONObject(i).getJSONArray("users").getJSONObject(j).getString("admin")));
                                    user.setUsername(response.getJSONObject(i).getJSONArray("users").getJSONObject(j).getString("username"));
                                    user.setFirstname(response.getJSONObject(i).getJSONArray("users").getJSONObject(j).getString("firstname"));
                                    user.setLastname(response.getJSONObject(i).getJSONArray("users").getJSONObject(j).getString("lastname"));
                                    users.add(j, user);
                                }
                                team.setUsers(users);
                                ArrayList<Schedule> schedules = new ArrayList<Schedule>();
                                for(int j=0; j<response.getJSONObject(i).getJSONArray("schedules").length(); j++){
                                    Schedule schedule = new Schedule();
                                    schedule.setId(Long.parseLong(response.getJSONObject(i).getJSONArray("schedules").getJSONObject(j).getString("id")));
                                    schedule.setTitle(response.getJSONObject(i).getJSONArray("schedules").getJSONObject(j).getString("title"));
                                    schedules.add(j,schedule);
                                }
                                team.setSchedules(schedules);
                                teams.add(i,team);
                                displayList.add(i,team.getTeamname());
                            }
                            ArrayAdapter adapter = new ArrayAdapter(activity_team.this, R.layout.custom_text, displayList){
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
                            ListView listview = (ListView) findViewById(R.id.teamList);
                            listview.setAdapter(adapter);

                            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id){
                                    System.out.println(teams.get(0).getUsers().size() + " svirka");
                                    Intent i = new Intent(activity_team.this, activity_team_details.class);
                                    i.putExtra("key2", position);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("key", teams);
                                    i.putExtras(bundle);
                                    startActivity(i);
                                }

                            });
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
        item.setTitle("Add New Team");
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
            case R.id.main_menu_item_1: startActivity(new Intent(activity_team.this, activity_create_team.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
