package cm.com.teamscheduler.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.utils.Auth;

public class activity_user extends AppCompatActivity {
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
        final ArrayList<User> users= new ArrayList<User>();
        final ArrayList<String> displayList= new ArrayList<String>();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++)
                            {
                                User user = new User();
                                user.setId(Long.parseLong(response.getJSONObject(i).getString("id")));
                                user.setFirstname(response.getJSONObject(i).getString("firstname"));
                                user.setLastname(response.getJSONObject(i).getString("lastname"));
                                user.setUsername(response.getJSONObject(i).getString("username"));
                                user.setPassword(response.getJSONObject(i).getString("password"));
                                user.setAdmin(Boolean.parseBoolean(response.getJSONObject(i).getString("admin")));
                                users.add(i,user);
                                displayList.add(i,user.getFirstname() + " " + user.getPassword() + " " + user.getAdmin());
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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_arry);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_text, displayList);
        ListView listview = (ListView) findViewById(R.id.listNoob);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                Intent i = new Intent(activity_user.this, activity_user_detailes.class);
                i.putExtra("key2", position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("key", users);
                i.putExtras(bundle);
                startActivity(i);
                }

        });
    }

}
