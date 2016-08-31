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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import cm.com.teamscheduler.app.entity.LocationItem;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by kostadin on 30.08.16.
 */
public class activity_location_items extends AppCompatActivity {
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    //END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_items_view);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Location Items List");

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
                        startActivity(new Intent(activity_location_items.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_location_items.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_location_items.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_location_items.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_location_items.this, activity_location.class));
                        break;
                }

                return false;
            }
        });

        //END OF MENU
        Bundle extras = getIntent().getExtras();
        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/LocationItem/getAll/" + extras.getLong("locationId");


        final ArrayList<LocationItem> locationItems= new ArrayList<LocationItem>();
        final ArrayList<String> displayList= new ArrayList<String>();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++)
                            {
                                LocationItem locationItem = new LocationItem();
                                locationItem.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                locationItem.setName(response.getJSONObject(i).getString("name"));
                                locationItem.setFloor(Integer.parseInt(response.getJSONObject(i).getString("floor")));
                                locationItem.setNumber(Integer.parseInt(response.getJSONObject(i).getString("number")));
                                locationItem.setDetails(response.getJSONObject(i).getString("details"));
                                locationItems.add(i,locationItem);
                                displayList.add(i,locationItem.getName());
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
        ListView listview = (ListView) findViewById(R.id.locationItemsList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                Intent i = new Intent(activity_location_items.this, activity_location_items_details.class);
                i.putExtra("key2", position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("key", locationItems);
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
        item.setTitle("Add New Location Item");
        item = menu.findItem(R.id.main_menu_item_2);
        item.setVisible(false);
        item = menu.findItem(R.id.main_menu_item_3);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }


    //OPTIONS MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_item_1: startActivity(new Intent(activity_location_items.this, activity_create_location_item.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
