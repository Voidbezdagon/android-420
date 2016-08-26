package cm.com.teamscheduler.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Position;

public class activity_position_detailes extends AppCompatActivity {

    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    //END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.position_details_view);

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
                        startActivity(new Intent(activity_position_detailes.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_position_detailes.this,scheduleCalendar.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_position_detailes.this, activity_position.class));
                        break;
                }

                return false;
            }
        });

        //END OF MENU
        Bundle extras = getIntent().getExtras();
        int position = -1;

        ArrayList<Position> positions = (ArrayList<Position>) getIntent().getSerializableExtra("key");
        position = extras.getInt("key2");
        //The key argument here must match that used in the other activity
        if(positions!=null) {

            TextView tv = (TextView) findViewById(R.id.position_id);
            tv.setText(positions.get(position).getId().toString());
            tv = (TextView) findViewById(R.id.position_name);
            tv.setText(positions.get(position).getName());
            tv = (TextView) findViewById(R.id.position_parentId);
            tv.setText(positions.get(position).getParentId().toString());
            tv = (TextView) findViewById(R.id.position_level);
            tv.setText(positions.get(position).getLevel().toString());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    //OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.main_menu_item_1);
        item.setTitle("Edit Position");
        item = menu.findItem(R.id.main_menu_item_2);
        item.setTitle("Delete Position");
        return super.onCreateOptionsMenu(menu);
    }


    //OPTIONS MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_item_1:startActivity(new Intent(activity_position_detailes.this, activity_user.class));
                break;
            case R.id.main_menu_item_2:startActivity(new Intent(activity_position_detailes.this, activity_position.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
