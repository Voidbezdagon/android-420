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
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Position;
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by kostadin on 30.08.16.
 */
public class activity_create_location_item extends AppCompatActivity {

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
                        startActivity(new Intent(activity_create_location_item.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_create_location_item.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_create_location_item.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_create_location_item.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_create_location_item.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_create_location_item.this, activity_team.class));
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

        Button save = (Button) findViewById(R.id.buttonSaveLocationItem);

        save.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                String tag_json_obj = "json_obj_req";
                String url = "http://10.0.2.2:8080/content/api/LocationItem/save";

                JSONObject js = new JSONObject();
                try
                {
                    EditText lina = (EditText) findViewById(R.id.locationItemFormName);
                    js.put("name", lina.getText().toString());
                    EditText lif = (EditText) findViewById(R.id.locationItemFormFloor);
                    js.put("floor", lif.getText().toString());
                    EditText linu = (EditText) findViewById(R.id.locationItemFormNumber);
                    js.put("number", linu.getText().toString());
                    EditText lid = (EditText) findViewById(R.id.locationItemFormDetails);
                    js.put("details", lid.getText().toString());
                    js.put("location", new JSONObject().put("id", getIntent().getLongExtra("locationId", 0)));

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
                                Intent i = new Intent(activity_create_location_item.this, activity_location.class);
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
