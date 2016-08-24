package cm.com.teamscheduler.app.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cm.com.teamscheduler.R;


public class activity_user_detailes extends AppCompatActivity {

    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    //END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detailes);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("User Profile");




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
                        startActivity(new Intent(activity_user_detailes.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_user_detailes.this,scheduleCalendar.class));
                        break;
                }

                return false;
            }
        });

        //END OF MENU
        Bundle extras = getIntent().getExtras();
        int position = -1;

        ArrayList<User> users = (ArrayList<User>) getIntent().getSerializableExtra("key");
        position = extras.getInt("key2");
            //The key argument here must match that used in the other activity
        if(users!=null) {

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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
