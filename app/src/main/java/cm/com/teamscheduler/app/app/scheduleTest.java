package cm.com.teamscheduler.app.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Schedule;
import cm.com.teamscheduler.app.entity.ScheduleReport;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by void on 22.08.16.
 */
public class scheduleTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);

        final ArrayList<Schedule> list= (ArrayList<Schedule>) getIntent().getSerializableExtra("schedules");
        final ArrayList<String> displayList = new ArrayList<String>();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Schedule s : list)
        {
            displayList.add(s.getTitle() + " " + s.getDescription());
        }


        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_text, displayList);
        ListView listview = (ListView) findViewById(R.id.listNoob);
        listview.setAdapter(adapter);

       listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                setContentView(R.layout.schedule_selected_view);
                TextView tv = (TextView) findViewById(R.id.schedule_title);
                tv.setText(list.get(position).getTitle().toString());
                TextView tv1 = (TextView) findViewById(R.id.schedule_description);
                tv1.setText(list.get(position).getDescription().toString());
                TextView tv2 = (TextView) findViewById(R.id.schedule_startdate);
                tv2.setText(sdf.format(list.get(position).getStartDate()));
                TextView tv3 = (TextView) findViewById(R.id.schedule_enddate);
                tv3.setText(sdf.format(list.get(position).getEndDate()));
                TextView tv4 = (TextView) findViewById(R.id.schedule_recurringtime);
                tv4.setText(list.get(position).getRecurringTime().toString() + " Days");
                TextView tv5 = (TextView) findViewById(R.id.schedule_assignedteam);
                tv5.setText(list.get(position).getAssignedTeam().getTeamname().toString());
                TextView tv6 = (TextView) findViewById(R.id.schedule_location);
                tv6.setText(list.get(position).getLocation().getName().toString());
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}
