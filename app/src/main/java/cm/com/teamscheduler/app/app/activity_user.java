package cm.com.teamscheduler.app.app;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;

public class activity_user extends AppCompatActivity {
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
    ArrayList<String> displayList;
    ArrayList<User> users;
    //END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("User List");

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
                        startActivity(new Intent(activity_user.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_user.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_user.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_user.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_user.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_user.this, activity_team.class));
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
                startActivity(new Intent(activity_user.this, activity_edit_logged_user.class));
            }
        });

        //END OF MENU

        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/User/getAll";

        final ArrayList<User> users= new ArrayList<User>();
        final ArrayList<String> displayList= new ArrayList<String>();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++)
                            {
                                User user = new User();
                                Position position = new Position();
                                user.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                user.setFirstname(response.getJSONObject(i).getString("firstname"));
                                user.setLastname(response.getJSONObject(i).getString("lastname"));
                                user.setUsername(response.getJSONObject(i).getString("username"));
                                user.setPassword(response.getJSONObject(i).getString("password"));
                                user.setAdmin(Boolean.parseBoolean(response.getJSONObject(i).getString("admin")));
                                position.setId(response.getJSONObject(i).getJSONObject("position").getLong("id"));
                                position.setName(response.getJSONObject(i).getJSONObject("position").getString("name"));
                                user.setPosition(position);
                                users.add(i,user);
                                displayList.add(i,user.getFirstname() + " " + user.getLastname()    );

                                ArrayAdapter adapter = new ArrayAdapter(activity_user.this, R.layout.custom_text, displayList){
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
                                ListView listview = (ListView) findViewById(R.id.listNoob);
                                listview.setAdapter(adapter);

                                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,
                                                            int position, long id){
                                        Intent i = new Intent(activity_user.this, activity_user_detailes.class);
                                        i.putExtra("key2", position);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("key", users);
                                        i.putExtras(bundle);
                                        startActivity(i);
                                    }
                                });
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
        item.setTitle("Add New User");
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
            case R.id.main_menu_item_1: startActivity(new Intent(activity_user.this, activity_create_user.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
