package cm.com.teamscheduler.app.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Location;
import cm.com.teamscheduler.app.entity.Position;

/**
 * Created by kostadin on 29.08.16.
 */
public class activity_location_details extends AppCompatActivity implements OnMapReadyCallback {
    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    //END

    private ArrayList<Location> locations = null;
    private int position = -1;
    private Long parentId= -1l;



    //MAP
    private GoogleMap mMap;
    private Context context = this;
    private Float lat;
    private Float lng;
    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_details);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Location");




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
                        startActivity(new Intent(activity_location_details.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_location_details.this,scheduleCalendar.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_location_details.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_location_details.this, activity_location.class));
                        break;
                }

                return false;
            }
        });

        //END OF MENU


        //MAP
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Bundle extras = getIntent().getExtras();

        locations = (ArrayList<Location>) getIntent().getSerializableExtra("key");
        position = extras.getInt("key2");

        //The key argument here must match that used in the other activity
        if(locations!=null) {

            TextView tv = (TextView) findViewById(R.id.location_id);
            tv.setText(locations.get(position).getId().toString());
            parentId=locations.get(position).getId();
            tv = (TextView) findViewById(R.id.location_name);
            tv.setText(locations.get(position).getName());
            tv = (TextView) findViewById(R.id.location_region);
            tv.setText(locations.get(position).getRegion());
            tv = (TextView) findViewById(R.id.location_city);
            tv.setText(locations.get(position).getCity());
            tv = (TextView) findViewById(R.id.location_zip);
            tv.setText(locations.get(position).getZip().toString());
            tv = (TextView) findViewById(R.id.location_street);
            tv.setText(locations.get(position).getStreet());
            tv = (TextView) findViewById(R.id.location_street_number);
            tv.setText(locations.get(position).getStreetNumber().toString());
            tv = (TextView) findViewById(R.id.location_details);
            tv.setText(locations.get(position).getDetails());
            lat = locations.get(position).getLat();
            lng = locations.get(position).getLng();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    //MAP
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng cord = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(cord).title("Location"));
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cord));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
    }

    //OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.main_menu_item_1);
        item.setTitle("Edit Location");
        item = menu.findItem(R.id.main_menu_item_2);
        item.setTitle("Delete Location");
        item = menu.findItem(R.id.main_menu_item_3);
        item.setTitle("View Location Items");
        return super.onCreateOptionsMenu(menu);
    }


    //OPTIONS MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_item_1:startActivity(new Intent(activity_location_details.this, activity_user.class));
                break;
            case R.id.main_menu_item_2:startActivity(new Intent(activity_location_details.this, activity_position.class));
                break;
            case R.id.main_menu_item_3:{
                Intent i = new Intent(activity_location_details.this, activity_location_items.class);
                i.putExtra("locationId", parentId);
                startActivity(i);
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
