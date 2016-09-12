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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Schedule;
import cm.com.teamscheduler.app.entity.ScheduleActivity;
import cm.com.teamscheduler.app.entity.ScheduleActivityReport;
import cm.com.teamscheduler.app.entity.ScheduleReport;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by kostadin on 07.09.16.
 */
public class activity_schedule_report extends AppCompatActivity {
    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    ImageView iv;
    TextView tv;
    //END
    Long scheduleId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_report);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Schedule Reports");


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
                        startActivity(new Intent(activity_schedule_report.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_schedule_report.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_schedule_report.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_schedule_report.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_schedule_report.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_schedule_report.this, activity_team.class));
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
                startActivity(new Intent(activity_schedule_report.this, activity_edit_logged_user.class));
            }
        });
        //END OF MENU



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat vsdf = new SimpleDateFormat("yyyy-MM-dd");
        Bundle extras = getIntent().getExtras();
        int position = -1;

        final ArrayList<String> displayActivities = new ArrayList<String>();
        final ArrayList<String> displayReports = new ArrayList<String>();
        Schedule schedule = (Schedule) getIntent().getSerializableExtra("schedule");
        for(ScheduleActivity item : schedule.getActivities()){
            displayActivities.add(item.getDescription());
        }
        for(ScheduleReport item : schedule.getReports()){
            String row = new String();
            row += item.getDescription() + " " + item.getDate().toString() + "\n";
            for(ScheduleActivityReport act : item.getActivityReports()) {
                row += act.getScheduleActivity().getDescription();
                if (item.getActivityReports() != null) {
                    for (ScheduleActivityReport sar : item.getActivityReports()) {
                        Date date1 = new Date();
                        Date date2 = new Date();
                        try{
                            date1 = vsdf.parse(vsdf.format(sar.getDate()));
                            date2 = vsdf.parse(vsdf.format(item.getDate()));
                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                        if (date1.getTime() == date2.getTime() && sar.isFinished()) {
                            row += " " + "Finished" + "\n";
                        } else {
                            row += " " + "Not Finished" + "\n";
                        }
                    }
                }
                else{
                    row+="GYZ";
                }
            }
            displayReports.add(row);
        }

        // Adding request to request queue
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_text, displayActivities){
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

        ArrayAdapter adapter2 = new ArrayAdapter(this, R.layout.custom_text, displayReports){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 300;
                view.setLayoutParams(params);

                return view;
            }
        };

        ListView listview = (ListView) findViewById(R.id.reportList);
        listview.setAdapter(adapter2);
        listview = (ListView) findViewById(R.id.activityList);
        listview.setAdapter(adapter);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }


}
