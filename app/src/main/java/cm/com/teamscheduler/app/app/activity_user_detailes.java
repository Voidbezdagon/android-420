package cm.com.teamscheduler.app.app;

import android.annotation.TargetApi;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;


public class activity_user_detailes extends AppCompatActivity {

    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    ImageView iv;
    TextView tv;
    //END
    Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detailes);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("User Profile");


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
                        startActivity(new Intent(activity_user_detailes.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_user_detailes.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_user_detailes.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_user_detailes.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_user_detailes.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_user_detailes.this, activity_team.class));
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
                startActivity(new Intent(activity_user_detailes.this, activity_edit_logged_user.class));
            }
        });
        //END OF MENU

        //CHECK IF WE GOT HERE FROM LIST VIEW OR FROM DETAILS VIEW
        Bundle extras = getIntent().getExtras();
        int position = -1;

        if (getIntent().getSerializableExtra("key") != null) {
            ArrayList<User> users = (ArrayList<User>) getIntent().getSerializableExtra("key");
            position = extras.getInt("key2");
            //The key argument here must match that used in the other activity
            if (users != null) {
                userId = users.get(position).getId();
                TextView tv = (TextView) findViewById(R.id.user_id);
                tv.setText(users.get(position).getId().toString());
                tv = (TextView) findViewById(R.id.user_firstname);
                tv.setText(users.get(position).getFirstname());
                tv = (TextView) findViewById(R.id.user_lastname);
                tv.setText(users.get(position).getLastname());
                tv = (TextView) findViewById(R.id.user_username);
                tv.setText(users.get(position).getUsername());
                tv = (TextView) findViewById(R.id.user_password);
                tv.setText(users.get(position).getPassword());
                tv = (TextView) findViewById(R.id.user_admin);
                tv.setText(users.get(position).getAdmin().toString());
                tv = (TextView) findViewById(R.id.user_position);
                final Long positionId = users.get(position).getPosition().getId();
                tv.setOnClickListener(new TextView.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity_user_detailes.this, activity_position_detailes.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("positionId", positionId);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                tv.setText(users.get(position).getPosition().getName());
                //Setting Avatar with a single GET request
                String tag_json_obj = "json_obj_req";
                String url2 = "http://10.0.2.2:8080/content/api/User/get/" + userId;
                System.out.println(url2);
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        url2, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    byte[] decodedString = Base64.decode(response.getString("avatar"), Base64.DEFAULT);
                                    Bitmap pic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                    iv = (ImageView) findViewById(R.id.avatar_detail);
                                    iv.setImageBitmap(pic);
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
            }
        }
        else
        {
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
                                userId = response.getLong("id");
                                TextView tv = (TextView) findViewById(R.id.user_id);
                                tv.setText(response.getString("id"));
                                tv = (TextView) findViewById(R.id.user_firstname);
                                tv.setText(response.getString("firstname"));
                                tv = (TextView) findViewById(R.id.user_lastname);
                                tv.setText(response.getString("lastname"));
                                tv = (TextView) findViewById(R.id.user_username);
                                tv.setText(response.getString("username"));
                                tv = (TextView) findViewById(R.id.user_password);
                                tv.setText(response.getString("password"));
                                tv = (TextView) findViewById(R.id.user_admin);
                                tv.setText(response.getString("admin"));
                                tv = (TextView) findViewById(R.id.user_position);
                                final Long positionId = response.getJSONObject("position").getLong("id");
                                tv.setOnClickListener(new TextView.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(activity_user_detailes.this, activity_position_detailes.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putLong("positionId", positionId);
                                        i.putExtras(bundle);
                                        startActivity(i);
                                    }
                                });
                                tv.setText(positionId.toString());
                                byte[] decodedString = Base64.decode(response.getString("avatar"), Base64.DEFAULT);
                                Bitmap pic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                iv = (ImageView) findViewById(R.id.avatar_detail);
                                iv.setImageBitmap(pic);
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
        item.setTitle("Edit User");
        item = menu.findItem(R.id.main_menu_item_2);
        item.setTitle("Delete User");
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
                    Intent i = new Intent(activity_user_detailes.this, activity_edit_user.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("userId", userId);
                    i.putExtras(bundle);
                    startActivity(i);
                }
                break;
            case R.id.main_menu_item_2:
                {
                    String tag_json_obj = "json_obj_req";
                    String url2 = "http://10.0.2.2:8080/content/api/User/delete/" + userId;
                    System.out.println(url2);
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                            url2, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    startActivity(new Intent(activity_user_detailes.this, activity_user.class));
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
