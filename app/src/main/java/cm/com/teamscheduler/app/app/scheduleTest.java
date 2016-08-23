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
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by void on 22.08.16.
 */
public class scheduleTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);

        class Schedule{
            Long id;
            String title;
            String description;
            Date startDate;
            Date endDate;
            Long recurringTime;
        }

        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/Schedule/getAll/1";
        final ArrayList<Schedule> list= new ArrayList<Schedule>();
        final ArrayList<String> displayList= new ArrayList<String>();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
                                list.add(i,item);
                                displayList.add(i,item.title + " " + item.description + " " + sdf.format(item.startDate) + " " + sdf.format(item.endDate)
                                + " " + Auth.getInstance().getLoggedUser().getUsername() + " " + Auth.getInstance().getLoggedUser().getAccesskey());
                            }
                            //t.setText(response.getJSONObject(1).getString("username") + " e golqm " + response.getJSONObject(0).getString("password"));
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
        listview.setAdapter(adapter);

        /*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                setContentView(R.layout.activity_user_detailes);
                TextView tv = (TextView) findViewById(R.id.user_id);
                tv.setText(users.get(position).android_id.toString());
                tv=(TextView) findViewById(R.id.user_firstname);
                tv.setText(users.get(position).android_firstname);
                tv=(TextView) findViewById(R.id.user_lastname);
                tv.setText(users.get(position).android_lastname);
                tv=(TextView) findViewById(R.id.user_username);
                tv.setText(users.get(position).android_username);
                tv=(TextView) findViewById(R.id.user_password);
                tv.setText(users.get(position).android_password);
            }
        });*/
    }

    public void loginButtonOnClick(View v){

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
