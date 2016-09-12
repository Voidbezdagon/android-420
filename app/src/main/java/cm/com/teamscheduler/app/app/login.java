package cm.com.teamscheduler.app.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Position;
import cm.com.teamscheduler.app.entity.User;
import cm.com.teamscheduler.app.utils.Auth;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Auth.getInstance().getLoggedUser() != null)
        {
            TextView tv = (TextView) findViewById(R.id.loginLabel);
            tv.setText(Auth.getInstance().getLoggedUser().getUsername());
        }
    }

    public void loginButtonOnClick(View v){
        final EditText username = (EditText)findViewById(R.id.usernameField);
        final EditText password = (EditText)findViewById(R.id.passwordField);

        String tag_json_obj = "json_obj_req";

        String url = "http://10.0.2.2:8080/content/api/User/login";

        JSONObject js = new JSONObject();
        try
        {
            js.put("username", username.getText().toString());
            js.put("password", password.getText().toString());
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
                        User user = new User();
                        try {
                            user.setId(Long.parseLong(response.getString("id")));
                            user.setFirstname(response.getString("firstname"));
                            user.setLastname(response.getString("lastname"));
                            user.setUsername(response.getString("username"));
                            user.setPassword(response.getString("password"));
                            user.setAdmin(Boolean.parseBoolean(response.getString("admin")));
                            user.setAccesskey(response.getString("accesskey"));
                            user.setAvatar(response.getString("avatar"));
                            //Set Position
                                Position pos = new Position();
                                pos.setId(Long.parseLong(response.getJSONObject("position").getString("id")));
                                pos.setLevel(Long.parseLong(response.getJSONObject("position").getString("level")));
                            user.setPosition(pos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            Auth.getInstance().setLoggedUser(user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (user.getAdmin() == false)
                        {
                            startActivity(new Intent(login.this, scheduleCalendar.class));
                        }
                        else
                        {
                            startActivity(new Intent(login.this, activity_user.class));
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("mazen hui");
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        System.out.println(jsonObjReq);
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
