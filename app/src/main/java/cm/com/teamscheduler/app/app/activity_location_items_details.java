package cm.com.teamscheduler.app.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.LocationItem;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by kostadin on 30.08.16.
 */
public class activity_location_items_details extends AppCompatActivity {
    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    //END
    Long locationItemId;

    private ArrayList<LocationItem> locationItems = null;
    private int position = -1;



    //MAP
    private GoogleMap mMap;
    private Context context = this;
    private Float lat;
    private Float lng;
    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_item_details);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Location Item Details");




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
                        startActivity(new Intent(activity_location_items_details.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_location_items_details.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_location_items_details.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_location_items_details.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_location_items_details.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_location_items_details.this, activity_team.class));
                        break;
                }

                return false;
            }
        });

        //END OF MENU




        Bundle extras = getIntent().getExtras();

        locationItems = (ArrayList<LocationItem>) getIntent().getSerializableExtra("key");
        position = extras.getInt("key2");

        //The key argument here must match that used in the other activity
        if(locationItems!=null) {
            locationItemId = locationItems.get(position).getId();
            TextView tv = (TextView) findViewById(R.id.location_item_id);
            tv.setText(locationItems.get(position).getId().toString());
            tv = (TextView) findViewById(R.id.location_item_name);
            tv.setText(locationItems.get(position).getName());
            tv = (TextView) findViewById(R.id.location_item_floor);
            tv.setText(locationItems.get(position).getFloor().toString());
            tv = (TextView) findViewById(R.id.location_item_number);
            tv.setText(locationItems.get(position).getNumber().toString());
            tv = (TextView) findViewById(R.id.location_item_details);
            tv.setText(locationItems.get(position).getDetails());
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
        item.setTitle("Edit Location Item");
        item = menu.findItem(R.id.main_menu_item_2);
        item.setTitle("Delete Location Item");
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
            case R.id.main_menu_item_1:
            {
                Intent i = new Intent(activity_location_items_details.this, activity_edit_location_item.class);
                Bundle bundle = new Bundle();
                bundle.putLong("locationItemId", locationItemId);
                i.putExtras(bundle);
                startActivity(i);
            }
            break;
            case R.id.main_menu_item_2:
            {
                String tag_json_obj = "json_obj_req";
                String url2 = "http://10.0.2.2:8080/content/api/LocationItem/delete/" + locationItemId;
                System.out.println(url2);
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                        url2, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                startActivity(new Intent(activity_location_items_details.this, activity_location.class));
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
