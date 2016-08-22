package cm.com.teamscheduler.app.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cm.com.teamscheduler.R;

public class login extends AppCompatActivity {
    // Tag used to cancel the request
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_login);
        String tag_json_arry = "json_array_req";

        String url = "http://10.0.2.2:8080/content/api/User/getAll";

        setContentView(R.layout.activity_loggedin);
        final ArrayList<String> noob= new ArrayList<String>();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++)
                            {
                                try {
                                    response.getJSONObject(i).getString("username");
                                } catch (Exception e) {
                                    //response.getJSONObject(i).toString();
                                    System.out.println(response.getInt(i));
                                }

                                noob.add(i,response.getJSONObject(i).getString("username"));
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
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_arry);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, noob);
        ListView listview = (ListView) findViewById(R.id.listNoob);
        listview.setAdapter(adapter);

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
