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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Position;
import cm.com.teamscheduler.app.utils.Auth;

public class activity_position_detailes extends AppCompatActivity {

    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    ImageView iv;
    TextView tv;
    //END
    Long positionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.position_details_view);

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
                        startActivity(new Intent(activity_position_detailes.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_position_detailes.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_position_detailes.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_position_detailes.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_position_detailes.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_position_detailes.this, activity_team.class));
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

        //CHECK IF WE GOT HERE FROM LIST VIEW OR FROM DETAILS VIEW
        Bundle extras = getIntent().getExtras();
        int position = -1;

        if (getIntent().getSerializableExtra("key") != null) {
            ArrayList<Position> positions = (ArrayList<Position>) getIntent().getSerializableExtra("key");
            position = extras.getInt("key2");
            //The key argument here must match that used in the other activity
            if (positions != null) {
                positionId = positions.get(position).getId();
                TextView tv = (TextView) findViewById(R.id.position_id);
                tv.setText(positions.get(position).getId().toString());
                tv = (TextView) findViewById(R.id.position_name);
                tv.setText(positions.get(position).getName());
                tv = (TextView) findViewById(R.id.position_parentId);
                tv.setText(positions.get(position).getParentId().toString());
                tv = (TextView) findViewById(R.id.position_level);
                tv.setText(positions.get(position).getLevel().toString());
            }
        }
        else
        {
            //Begin get position request
            String tag_json_obj = "json_obj_req";
            String url2 = "http://10.0.2.2:8080/content/api/Position/get/" + getIntent().getLongExtra("positionId", 0);
            System.out.println(url2);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url2, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                positionId = Long.parseLong(response.getString("id"));
                                TextView tv = (TextView) findViewById(R.id.position_id);
                                tv.setText(response.getString("id"));
                                tv = (TextView) findViewById(R.id.position_name);
                                tv.setText(response.getString("name"));
                                tv = (TextView) findViewById(R.id.position_parentId);
                                tv.setText(response.getString("parentId"));
                                tv = (TextView) findViewById(R.id.position_level);
                                tv.setText(response.getString("level"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("mazen hui");
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

            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
            //End get position request
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
        item.setTitle("Edit Position");
        item = menu.findItem(R.id.main_menu_item_2);
        item.setTitle("Delete Position");
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
                    Intent i = new Intent(activity_position_detailes.this, activity_edit_position.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("positionId", positionId);
                    i.putExtras(bundle);
                    startActivity(i);
                }
                break;
            case R.id.main_menu_item_2:
                {
                    String tag_json_obj = "json_obj_req";
                    String url2 = "http://10.0.2.2:8080/content/api/Position/delete/" + positionId;
                    System.out.println(url2);
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                            url2, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    startActivity(new Intent(activity_position_detailes.this, activity_position.class));
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
