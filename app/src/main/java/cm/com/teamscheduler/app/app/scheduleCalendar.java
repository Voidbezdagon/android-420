package cm.com.teamscheduler.app.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by void on 22.08.16.
 */
public class scheduleCalendar extends Activity {

    //FOR MENU
    String[] menu;
    DrawerLayout dLayout;
    ListView dList;
    ArrayAdapter<String> adapterMenu;
    //END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        //MENU
        menu = new String[]{"User List","Calendar"};
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);
        adapterMenu = new ArrayAdapter<String>(this, R.layout.custom_menu_text,menu);

        dList.setAdapter(adapterMenu);

        dList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            @TargetApi(16)
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

                dLayout.closeDrawers();
                switch (position){
                    case 0:{
                        startActivity(new Intent(scheduleCalendar.this,activity_user.class));
                        break;
                    }
                    case 1:{
                        startActivity(new Intent(scheduleCalendar.this,scheduleCalendar.class));
                        break;
                    }
                }

//                Bundle args = new Bundle();
//                args.putString("Menu", menu[position]);
//                Fragment detail = new DetailFragment();
//                detail.setArguments(args);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.content_frame, detail).commit();

            }

        });

        //END OF MENU


        class Report{
            Date date;
        }

        class Schedule{
            Long id;
            String title;
            String description;
            Date startDate;
            Date endDate;
            Long recurringTime;
            Report report;
        }

        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/Schedule/getAll/";
        final ArrayList<Schedule> list= new ArrayList<Schedule>();
        final ArrayList<String> displayList= new ArrayList<String>();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat vsdf = new SimpleDateFormat("yyyy-MM-dd");

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++)
                            {
                                Schedule item = new Schedule();
                                item.id=Long.parseLong(response.getJSONObject(i).getString("id"));
                                item.title=response.getJSONObject(i).getString("title");
                                item.description=response.getJSONObject(i).getString("description");
                                item.startDate=new Date(Long.parseLong(response.getJSONObject(i).getString("startDate")));
                                item.endDate = new Date(Long.parseLong(response.getJSONObject(i).getString("endDate")));
                                item.recurringTime=Long.parseLong(response.getJSONObject(i).getString("recurringTime"));
                                Report rep = new Report();
                                try {
                                    if (response.getJSONObject(i).getJSONArray("reports").length() > 0) {
                                        rep.date = vsdf.parse(vsdf.format(new Date(Long.parseLong(response.getJSONObject(i).getJSONArray("reports").getJSONObject(0).getString("date")))));
                                    }
                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                                item.report = rep;
                                list.add(i,item);
                                displayList.add(i,item.title + " " + item.description + " " + sdf.format(item.startDate) + " " + sdf.format(item.endDate));
                                try {
                                    for (long j = vsdf.parse(vsdf.format(item.startDate)).getTime(); j <= vsdf.parse(vsdf.format(item.endDate)).getTime(); j = j + (item.recurringTime * 86400000)) {
                                        if (item.report.date != null) {
                                            if (item.report.date.getTime() == j) {
                                                Event ev = new Event(Color.RED, j);
                                                compactCalendarView.addEvent(ev);
                                            }
                                        }
                                        else
                                        {
                                            Event ev = new Event(Color.GREEN, j);
                                            compactCalendarView.addEvent(ev);
                                        }
                                    }
                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
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
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_text, displayList);
        ListView listview = (ListView) findViewById(R.id.listNoob);
        //listview.setAdapter(adapter);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                startActivity(new Intent(scheduleCalendar.this, scheduleTest.class));
                //List<Event> events = compactCalendarView.getEvents(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });
    }
}
