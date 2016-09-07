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
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import cm.com.teamscheduler.app.entity.ScheduleActivity;
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by kostadin on 30.08.16.
 */
public class activity_create_team extends AppCompatActivity {
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
        setContentView(R.layout.team_form);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Create Team");

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
                        startActivity(new Intent(activity_create_team.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_create_team.this,scheduleCalendar.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_create_team.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_create_team.this, activity_location.class));
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

        //Get Users and feed them to Multi-select
        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/User/getAll";

        final ArrayList<User> list = new ArrayList<User>();
        final ArrayList<String> displayList = new ArrayList<String>();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                //Parsing Position from JSON to Entity
                                User item = new User();
                                item.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                item.setFirstname(response.getJSONObject(i).getString("firstname"));
                                item.setLastname(response.getJSONObject(i).getString("lastname"));
                                list.add(i, item);
                                displayList.add(i,response.getJSONObject(i).getString("firstname") + " " + response.getJSONObject(i).getString("lastname"));
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

        AppController.getInstance().addToRequestQueue(req, tag_json_arry);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, displayList);

        final ListView listview = (ListView) findViewById(R.id.teamFormUsers);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(adapter);

        //End get Users and feed them to Multi-select


        Button save = (Button) findViewById(R.id.buttonSaveTeam);

        save.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                String tag_json_obj = "json_obj_req";
                String url = "http://10.0.2.2:8080/content/api/Team/save";
                SparseBooleanArray sparseBooleanArray = listview.getCheckedItemPositions();

                ArrayList<User> checkedUsers = new ArrayList<User>();
                for (int i = 0; i < list.size(); i++)
                {
                    if (sparseBooleanArray.get(i))
                    {
                        checkedUsers.add(list.get(i));
                    }
                }

                JSONArray arr = new JSONArray();
                try
                {
                    for (User u : checkedUsers)
                    {
                        arr.put(new JSONObject()
                                .put("id", u.getId()));
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                JSONObject js = new JSONObject();
                try
                {
                    EditText tfn = (EditText) findViewById(R.id.teamFormName);
                    js.put("teamname", tfn.getText().toString());
                    js.put("users", arr);

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
                                Intent i = new Intent(activity_create_team.this, activity_team.class);
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
