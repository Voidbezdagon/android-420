package cm.com.teamscheduler.app.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cm.com.teamscheduler.R;


public class activity_user_detailes extends AppCompatActivity {

    //FOR MENU
    String[] menu;
    DrawerLayout dLayout;
    ListView dList;
    ArrayAdapter<String> adapterMenu;
    ActionBarDrawerToggle actionBarDrawerToggle;
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
        menu = new String[]{"User List","Calendar"};
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,dLayout, toolbar, R.string.drawer_open, R.string.drawer_close );
        dList = (ListView) findViewById(R.id.left_drawer);
        adapterMenu = new ArrayAdapter<String>(this, R.layout.custom_menu_text,menu);



        dLayout.addDrawerListener(actionBarDrawerToggle);

        dList.setAdapter(adapterMenu);


        dList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            @TargetApi(16)
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

                dLayout.closeDrawers();
                switch (position) {
                    case 0: {
                        startActivity(new Intent(activity_user_detailes.this, activity_user.class));
                        break;
                    }
                    case 1: {
                        startActivity(new Intent(activity_user_detailes.this, scheduleCalendar.class));
                        break;
                    }
                }
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
