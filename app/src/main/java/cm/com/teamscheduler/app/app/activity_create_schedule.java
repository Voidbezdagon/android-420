package cm.com.teamscheduler.app.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Location;
import cm.com.teamscheduler.app.entity.Position;
import cm.com.teamscheduler.app.entity.Team;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by kostadin on 30.08.16.
 */
public class activity_create_schedule extends AppCompatActivity{
    ArrayList<String> teamList;
    ArrayList<String> locationList;
    Spinner spinnerTeam;
    Spinner spinnerLocation;
    ArrayAdapter<String> adapterTeam;
    ArrayAdapter<String> adapterLocation;
    ArrayList<Team> teams;
    ArrayList<Location> locations;
    static Long startDate = new Long(0);
    static Long startTime = new Long(0);
    static Long endDate = new Long(0);
    static Long endTime = new Long(0);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    //END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_create_form);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Create User");

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
                        startActivity(new Intent(activity_create_schedule.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_create_schedule.this,scheduleCalendar.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_create_schedule.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_create_schedule.this, activity_location.class));
                        break;
                }

                return false;
            }
        });
        //END OF MENU

        //Populate Team Dropdown from JSON
        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/Team/getAll";
        teams = new ArrayList<Team>();
        teamList = new ArrayList<String>();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                //Parsing Team from JSON to Entity
                                Team item = new Team();
                                item.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                item.setTeamname(response.getJSONObject(i).getString("teamname"));
                                teams.add(i, item);
                                teamList.add(i,response.getJSONObject(i).getString("teamname"));
                            }
                            onSuccessResponseTeam();
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
        //End of JSON Team GetAll request

        //Populate Location Dropdown from JSON
        String tag_json_arry3 = "json_array_req";

        String url3 = "http://10.0.2.2:8080/content/api/Location/getAll";
        locations = new ArrayList<Location>();
        locationList = new ArrayList<String>();

        JsonArrayRequest req3 = new JsonArrayRequest(url3,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                //Parsing Location from JSON to Entity
                                Location item = new Location();
                                item.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                item.setName(response.getJSONObject(i).getString("name"));
                                locations.add(i, item);
                                locationList.add(i,response.getJSONObject(i).getString("name"));
                            }
                            onSuccessResponseLocation();
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

        AppController.getInstance().addToRequestQueue(req3, tag_json_arry3);
        //End of JSON Position GetAll request

        Button save = (Button) findViewById(R.id.buttonSaveSchedule);

        save.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                String tag_json_obj = "json_obj_req";
                String url = "http://10.0.2.2:8080/content/api/Schedule/save";

                JSONObject js = new JSONObject();
                try
                {
                    EditText st = (EditText) findViewById(R.id.scheduleFormTitle);
                    js.put("title", st.getText().toString());
                    EditText sd = (EditText) findViewById(R.id.scheduleFormDescription);
                    js.put("description", sd.getText().toString());
                    EditText srt = (EditText) findViewById(R.id.scheduleFormRecurringTime);
                    js.put("recurringTime", srt.getText().toString());
                    js.put("assignedTeam", new JSONObject().put("id", getTeamId(spinnerTeam.getSelectedItem().toString())));
                    js.put("location", new JSONObject().put("id", getLocationId(spinnerLocation.getSelectedItem().toString())));
                    js.put("startDate", startDate + startTime);
                    js.put("endDate", endDate + endTime);

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
                                Intent i = new Intent(activity_create_schedule.this, activity_schedule.class);
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

    private void onSuccessResponseTeam()
    {
        spinnerTeam = (Spinner) findViewById(R.id.scheduleFormAssignedTeam);
        adapterTeam = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teamList);
        adapterTeam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeam.setAdapter(adapterTeam);
    }

    private void onSuccessResponseLocation()
    {
        spinnerLocation = (Spinner) findViewById(R.id.scheduleFormLocation);
        adapterLocation = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationList);
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapterLocation);
    }

    private Long getTeamId(String name)
    {
        for (Team team : teams)
        {
            if (name.equals(team.getTeamname()))
                return team.getId();
        }
        return null;
    }

    private Long getLocationId(String name)
    {
        for (Location l : locations)
        {
            if (name.equals(l.getName()))
                return l.getId();
        }
        return null;
    }

    //START TIME PICKERS AND ONCLICK EVENTS
    public static class StartTimePicker extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(),TimePickerDialog.THEME_HOLO_DARK , this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startTime = new Long((hourOfDay * 3600000) + (minute * 60000));
        }
    }

    public static class StartDatePicker extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),DatePickerDialog.THEME_HOLO_DARK , this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 0, 0);
            startDate = c.getTimeInMillis();
        }
    }

    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new StartTimePicker();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showStartDatePickerDialog(View v) {
        DialogFragment newFragment = new StartDatePicker();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //END TIME PICKERS AND ONCLICK EVENTS
    public static class EndTimePicker extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), TimePickerDialog.THEME_HOLO_DARK , this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endTime = new Long((hourOfDay * 3600000) + (minute * 60000));
        }
    }

    public static class EndDatePicker extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), TimePickerDialog.THEME_HOLO_DARK, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 0, 0);
            endDate = c.getTimeInMillis();
        }
    }

    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new EndTimePicker();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showEndDatePickerDialog(View v) {
        DialogFragment newFragment = new EndDatePicker();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
