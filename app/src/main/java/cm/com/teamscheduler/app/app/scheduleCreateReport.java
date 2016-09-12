package cm.com.teamscheduler.app.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Position;
import cm.com.teamscheduler.app.entity.Schedule;
import cm.com.teamscheduler.app.entity.ScheduleActivity;
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by void on 26.08.16.
 */
public class scheduleCreateReport extends AppCompatActivity {

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
        setContentView(R.layout.schedule_form);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Create Report");

        //MENU & TOOLBAR
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,dLayout, toolbar, R.string.drawer_open, R.string.drawer_close );
        dLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        if (Auth.getInstance().getLoggedUser().getAdmin() == false)
        {
            MenuItem item;
            item = (MenuItem) navigationView.getMenu().findItem(R.id.user_list_view);
            item.setVisible(false);
            item = (MenuItem) navigationView.getMenu().findItem(R.id.schedule_view);
            item.setVisible(false);
            item = (MenuItem) navigationView.getMenu().findItem(R.id.positions_view);
            item.setVisible(false);
            item = (MenuItem) navigationView.getMenu().findItem(R.id.location_view);
            item.setVisible(false);
            item = (MenuItem) navigationView.getMenu().findItem(R.id.team_view);
            item.setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.user_list_view:
                        startActivity(new Intent(scheduleCreateReport.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(scheduleCreateReport.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(scheduleCreateReport.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(scheduleCreateReport.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(scheduleCreateReport.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(scheduleCreateReport.this, activity_team.class));
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
                startActivity(new Intent(scheduleCreateReport.this, activity_edit_logged_user.class));
            }
        });

        //END OF MENU

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final Schedule schedule = (Schedule) getIntent().getSerializableExtra("schedule");
        final ArrayList<String> displayList = new ArrayList<String>();
        final Button submitReport = (Button) findViewById(R.id.buttonSubmitReport);

        for (ScheduleActivity sa : schedule.getActivities())
        {
            displayList.add(sa.getDescription());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, displayList);

        final ListView listview = (ListView) findViewById(R.id.listScheduleActivities);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(adapter);

        submitReport.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                final EditText description = (EditText)findViewById(R.id.scheduleFormDescription);
                String tag_json_obj = "json_obj_req";
                String url = "http://10.0.2.2:8080/content/api/ScheduleReport/save";
                SparseBooleanArray sparseBooleanArray = listview.getCheckedItemPositions();

                ArrayList<ScheduleActivity> checkedActivities = new ArrayList<ScheduleActivity>();
                for (int i = 0; i < schedule.getActivities().size(); i++)
                {
                    if (sparseBooleanArray.get(i))
                    {
                        checkedActivities.add(schedule.getActivities().get(i));
                    }
                }

                JSONArray jsActivityReports = new JSONArray();
                try
                {
                    for (ScheduleActivity sa : checkedActivities)
                    {
                        jsActivityReports.put(new JSONObject()
                                .put("id", sa.getId())
                                .put("isFinished", true)
                                .put("scheduleActivity", new JSONObject()
                                        .put("id", sa.getId())));
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                JSONObject js = new JSONObject();
                try
                {
                    js.put("id", schedule.getId());
                    js.put("description", description.getText().toString());
                    js.put("schedule", new JSONObject().put("id", schedule.getId()));
                    js.put("activityReports", jsActivityReports);
                    try {
                        js.put("date", sdf.parse(sdf.format(new Date())).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
                                Intent i = new Intent(scheduleCreateReport.this, scheduleCalendar.class);
                                startActivity(i);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers));
                                // Now you can use any deserializer to make sense of data
                                System.out.println(res);
                                JSONObject obj = new JSONObject(res);
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                System.out.println("mazen hui1");
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                System.out.println("mazen hui2");
                                e2.printStackTrace();
                            }
                        }
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
