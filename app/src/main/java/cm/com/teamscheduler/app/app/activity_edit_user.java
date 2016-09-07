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
 * Created by void on 30.08.16.
 */
public class activity_edit_user extends AppCompatActivity {
    ArrayList<String> displayList;
    ArrayList<String> adminList;
    Spinner spinnerPosition;
    Spinner spinnerAdmin;
    ArrayAdapter<String> adapterPosition;
    ArrayAdapter<String> adapterAdmin;
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
        setContentView(R.layout.user_form);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Create User");

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
                        startActivity(new Intent(activity_edit_user.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_edit_user.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_edit_user.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_edit_user.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_edit_user.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_edit_user.this, activity_team.class));
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
        adminList= new ArrayList<String>();

        adminList.add(0,"false");
        adminList.add(1,"true");

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
                                displayList.add(i,response.getJSONObject(i).getString("name"));
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

        //Begin get user request
        String tag_json_obj = "json_obj_req";
        String url2 = "http://10.0.2.2:8080/content/api/User/get/" + getIntent().getLongExtra("userId", 0);
        System.out.println(url2);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            EditText un = (EditText) findViewById(R.id.userFormUsername);
                            un.setText(response.getString("username"));
                            EditText pw = (EditText) findViewById(R.id.userFormPassword);
                            pw.setText(response.getString("password"));
                            EditText fn = (EditText) findViewById(R.id.userFormFirstName);
                            fn.setText(response.getString("firstname"));
                            EditText ln = (EditText) findViewById(R.id.userFormLastName);
                            ln.setText(response.getString("lastname"));
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
        //End get user request

        //Begin submit Edit request
        Button submitReport = (Button) findViewById(R.id.buttonSaveUser);

        submitReport.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                final EditText description = (EditText)findViewById(R.id.scheduleFormDescription);
                String tag_json_obj = "json_obj_req";
                String url = "http://10.0.2.2:8080/content/api/User/save";

                JSONObject js = new JSONObject();
                try
                {
                    js.put("id", getIntent().getLongExtra("userId", 0));
                    EditText un = (EditText) findViewById(R.id.userFormUsername);
                    js.put("username", un.getText().toString());
                    EditText pw = (EditText) findViewById(R.id.userFormPassword);
                    js.put("password", pw.getText().toString());
                    EditText fn = (EditText) findViewById(R.id.userFormFirstName);
                    js.put("firstname", fn.getText().toString());
                    EditText ln = (EditText) findViewById(R.id.userFormLastName);
                    js.put("lastname", ln.getText().toString());
                    js.put("admin", spinnerAdmin.getSelectedItem().toString());
                    js.put("position", new JSONObject().put("id", getPositionId(spinnerPosition.getSelectedItem().toString())));

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
                                Intent i = new Intent(activity_edit_user.this, activity_user.class);
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
        //End submit Edit request
    }
    //DRAWER MENU
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private void onSuccessResponse()
    {
        spinnerPosition = (Spinner) findViewById(R.id.userFormPosition);
        adapterPosition = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, displayList);
        adapterPosition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(adapterPosition);

        spinnerAdmin = (Spinner) findViewById(R.id.userFormAdmin);
        adapterAdmin = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, adminList);
        adapterAdmin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdmin.setAdapter(adapterAdmin);
    }

    private Long getPositionId(String name)
    {
        for (Position pos : list)
        {
            if (name.equals(pos.getName()))
                return pos.getId();
        }
        return null;
    }
}
