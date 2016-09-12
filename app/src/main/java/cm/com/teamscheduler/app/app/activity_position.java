package cm.com.teamscheduler.app.app;

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
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;


public class activity_position extends AppCompatActivity {
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
        setContentView(R.layout.pisotion_view);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Positions List");

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
                        startActivity(new Intent(activity_position.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_position.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_position.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_position.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_position.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_position.this, activity_team.class));
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
                startActivity(new Intent(activity_position.this, activity_edit_logged_user.class));
            }
        });
        //END OF MENU

        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/Position/getAll";


        final ArrayList<Position> positions= new ArrayList<Position>();
        final ArrayList<String> displayList= new ArrayList<String>();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++)
                            {
                                Position position = new Position();
                                position.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                position.setParentId(Long.parseLong(response.getJSONObject(i).getString("parentId")));
                                position.setLevel(Long.parseLong(response.getJSONObject(i).getString("level")));
                                position.setName(response.getJSONObject(i).getString("name"));
                                positions.add(i,position);
                                displayList.add(i,position.getName());

                                ArrayAdapter adapter = new ArrayAdapter(activity_position.this, R.layout.custom_text, displayList){
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
                                ListView listview = (ListView) findViewById(R.id.positionList);
                                listview.setAdapter(adapter);

                                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,
                                                            int position, long id){
                                        Intent i = new Intent(activity_position.this, activity_position_detailes.class);
                                        i.putExtra("key2", position);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("key", positions);
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
        item.setTitle("Add New Position");
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
            case R.id.main_menu_item_1: startActivity(new Intent(activity_position.this, activity_create_position.class));
        }

        return super.onOptionsItemSelected(item);
    }
}

