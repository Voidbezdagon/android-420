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
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by kostadin on 26.08.16.
 */
public class activity_create_position extends AppCompatActivity {

    ArrayList<String> displayList;
    Spinner spinnerPosition;
    ArrayAdapter<String> adapterPosition;
    ArrayList<Position> list;

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
        setContentView(R.layout.position_form);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Create Position");

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
                        startActivity(new Intent(activity_create_position.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_create_position.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_create_position.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_create_position.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_create_position.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_create_position.this, activity_team.class));
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

        //Populate Position Dropdown from JSON
        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/Position/getAll";
        list= new ArrayList<Position>();
        displayList = new ArrayList<String>();
        displayList.add(0, "None");

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                //Parsing Position from JSON to Entity
                                Position item = new Position();
                                item.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                item.setName(response.getJSONObject(i).getString("name"));
                                list.add(i, item);
                                displayList.add(i + 1,response.getJSONObject(i).getString("name"));
                            }
                            onSuccessResponse();
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

        AppController.getInstance().addToRequestQueue(req, tag_json_arry);
        //End of JSON Position GetAll request

        Button save = (Button) findViewById(R.id.buttonSavePosition);

        save.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                String tag_json_obj = "json_obj_req";
                String url = "http://10.0.2.2:8080/content/api/Position/save";

                JSONObject js = new JSONObject();
                try
                {
                    EditText pn = (EditText) findViewById(R.id.positionFormName);
                    js.put("name", pn.getText().toString());
                    js.put("parentId", getPositionId(spinnerPosition.getSelectedItem().toString()));

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
                                Intent i = new Intent(activity_create_position.this, activity_position.class);
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

    private void onSuccessResponse()
    {
        spinnerPosition = (Spinner) findViewById(R.id.positionFormParentId);
        adapterPosition = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, displayList);
        adapterPosition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(adapterPosition);
    }

    private Long getPositionId(String name)
    {
        for (Position pos : list)
        {
            if (name.equals(pos.getName()))
                return pos.getId();
        }
        return 0l;
    }
}
