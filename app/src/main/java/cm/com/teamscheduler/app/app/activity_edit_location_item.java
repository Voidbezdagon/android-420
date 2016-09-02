package cm.com.teamscheduler.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by void on 01.09.16.
 */
public class activity_edit_location_item extends AppCompatActivity {
    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    //END

    Long locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_item_form);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Create Location Item");

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
                        startActivity(new Intent(activity_edit_location_item.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_edit_location_item.this,scheduleCalendar.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_edit_location_item.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_edit_location_item.this, activity_location.class));
                        break;
                }
                return false;
            }
        });
        //END OF MENU

        //Begin get location item request
        String tag_json_obj = "json_obj_req";
        String url2 = "http://10.0.2.2:8080/content/api/LocationItem/get/" + getIntent().getLongExtra("locationItemId", 0);
        System.out.println(url2);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            EditText lina = (EditText) findViewById(R.id.locationItemFormName);
                            lina.setText(response.getString("name"));
                            EditText lif = (EditText) findViewById(R.id.locationItemFormFloor);
                            lif.setText(response.getString("floor"));
                            EditText linu = (EditText) findViewById(R.id.locationItemFormNumber);
                            linu.setText(response.getString("number"));
                            EditText lid = (EditText) findViewById(R.id.locationItemFormDetails);
                            lid.setText(response.getString("details"));
                            locationId = response.getJSONObject("location").getLong("id");
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
        //End get location item request

        Button save = (Button) findViewById(R.id.buttonSaveLocationItem);

        save.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                String tag_json_obj = "json_obj_req";
                String url = "http://10.0.2.2:8080/content/api/LocationItem/save";

                JSONObject js = new JSONObject();
                try
                {
                    js.put("id", getIntent().getLongExtra("locationItemId", 0));
                    EditText lina = (EditText) findViewById(R.id.locationItemFormName);
                    js.put("name", lina.getText().toString());
                    EditText lif = (EditText) findViewById(R.id.locationItemFormFloor);
                    js.put("floor", lif.getText().toString());
                    EditText linu = (EditText) findViewById(R.id.locationItemFormNumber);
                    js.put("number", linu.getText().toString());
                    EditText lid = (EditText) findViewById(R.id.locationItemFormDetails);
                    js.put("details", lid.getText().toString());
                    js.put("location", new JSONObject().put("id", locationId));

                    System.out.println(js);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        url, js,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent i = new Intent(activity_edit_location_item.this, activity_location.class);
                                startActivity(i);
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
                //System.out.println(jsonObjReq);
            }
        });
    }

    //DRAWER MENU
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
