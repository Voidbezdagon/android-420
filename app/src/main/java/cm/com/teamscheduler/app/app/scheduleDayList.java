package cm.com.teamscheduler.app.app;

import android.content.Intent;
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
import java.util.List;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Position;
import cm.com.teamscheduler.app.entity.Schedule;
import cm.com.teamscheduler.app.entity.ScheduleReport;
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by void on 22.08.16.
 */
public class scheduleDayList extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);

        final ArrayList<Schedule> list= (ArrayList<Schedule>) getIntent().getSerializableExtra("schedules");
        final ArrayList<String> displayList = new ArrayList<String>();
        final Long dateClicked = (Long) getIntent().getSerializableExtra("dateClicked");
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Schedule s : list)
        {
            displayList.add(s.getTitle() + " " + s.getDescription());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_text, displayList);
        ListView listview = (ListView) findViewById(R.id.listNoob);
        //listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); android.R.layout.simple_list_item_multiple_choice
        listview.setAdapter(adapter);

       listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(scheduleDayList.this, scheduleDetails.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("schedule", list.get(position));
                bundle.putLong("dateClicked", dateClicked);
                i.putExtras(bundle);
                startActivity(i);
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
