package cm.com.teamscheduler.app.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cm.com.teamscheduler.R;

/**
 * Created by kostadin on 22.08.16.
 */
public class activity_user_detailes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        int position = -1;

        ArrayList<User> users = (ArrayList<User>) getIntent().getSerializableExtra("key");
        position = extras.getInt("key2");
            //The key argument here must match that used in the other activity
        if(users!=null) {
            setContentView(R.layout.activity_user_detailes);
            TextView tv = (TextView) findViewById(R.id.user_id);
            tv.setText(users.get(position).getId().toString());
            tv = (TextView) findViewById(R.id.user_firstname);
            tv.setText(users.get(position).getFirstname());
            tv = (TextView) findViewById(R.id.user_lastname);
            tv.setText(users.get(position).getLastname());
            tv = (TextView) findViewById(R.id.user_username);
            tv.setText(users.get(position).getUsername());
            tv = (TextView) findViewById(R.id.user_password);
            tv.setText(users.get(position).getPassword());
            tv = (TextView) findViewById(R.id.user_admin);
            tv.setText(users.get(position).getAdmin().toString());
        }
    }
}
