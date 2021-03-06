package cm.com.teamscheduler.app.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.com.teamscheduler.R;
import cm.com.teamscheduler.app.entity.Position;
import cm.com.teamscheduler.app.utils.Auth;

/**
 * Created by kostadin on 29.08.16.
 */
public class activity_create_location extends AppCompatActivity implements OnMapReadyCallback {
    ArrayList<Position> list;
    private GoogleMap mMap;
    private Double lat;
    private Double lng;

    //FOR MENU
    DrawerLayout dLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    ImageView iv;
    TextView tv;
    //END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("User List");

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
                        startActivity(new Intent(activity_create_location.this, activity_user.class));
                        break;
                    case R.id.calendar_view:
                        startActivity(new Intent(activity_create_location.this,scheduleCalendar.class));
                        break;
                    case R.id.schedule_view:
                        startActivity(new Intent(activity_create_location.this,activity_schedule.class));
                        break;
                    case R.id.positions_view:
                        startActivity(new Intent(activity_create_location.this, activity_position.class));
                        break;
                    case R.id.location_view:
                        startActivity(new Intent(activity_create_location.this, activity_location.class));
                        break;
                    case R.id.team_view:
                        startActivity(new Intent(activity_create_location.this, activity_team.class));
                        break;
                }

                return false;
            }
        });
        byte[] decodedString = Base64.decode(Auth.getInstance().getLoggedUser().getAvatar(), Base64.DEFAULT);
        Bitmap pic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        View view = navigationView.inflateHeaderView(R.layout.navigarion_drawer_header);

        iv = (ImageView) view.findViewById(R.id.avatar);
        iv.setImageBitmap(pic);

        tv = (TextView) view.findViewById(R.id.header_name);
        tv.setText(Auth.getInstance().getLoggedUser().getUsername());

        tv = (TextView) view.findViewById(R.id.header_subname);
        tv.setText(Auth.getInstance().getLoggedUser().getFirstname() + " " + Auth.getInstance().getLoggedUser().getLastname());

        iv.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_create_location.this, activity_edit_logged_user.class));
            }
        });

        //END OF MENU

        //Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.locationFormMap);
        mapFragment.getMapAsync(this);

        Button save = (Button) findViewById(R.id.buttonSaveLocation);

        save.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                mapSearch();

                new CountDownTimer(3000, 3000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        String tag_json_obj = "json_obj_req";
                        String url = "http://10.0.2.2:8080/content/api/Location/save";

                        JSONObject js = new JSONObject();
                        try
                        {
                            EditText lfn = (EditText) findViewById(R.id.locationFormName);
                            js.put("name", lfn.getText().toString());
                            EditText lfr = (EditText) findViewById(R.id.locationFormRegion);
                            js.put("region", lfr.getText().toString());
                            EditText lfc = (EditText) findViewById(R.id.locationFormCity);
                            js.put("city", lfc.getText().toString());
                            EditText lfz = (EditText) findViewById(R.id.locationFormZip);
                            js.put("zip", lfz.getText().toString());
                            EditText lfs = (EditText) findViewById(R.id.locationFormStreet);
                            js.put("street", lfs.getText().toString());
                            EditText lfsn = (EditText) findViewById(R.id.locationFormStreetNumber);
                            js.put("streetNumber", lfsn.getText().toString());
                            EditText lfd = (EditText) findViewById(R.id.locationFormDetails);
                            js.put("details", lfd.getText().toString());
                            js.put("lat", lat);
                            js.put("lng", lng);
                            System.out.println(js);
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
                                        Intent i = new Intent(activity_create_location.this, activity_location.class);
                                        startActivity(i);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("mazen hui");
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json");
                                headers.put("access-key", Auth.getInstance().getLoggedUser().getAccesskey());
                                return headers;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
                    }
                }.start();
            }
        });
    }

    private void mapSearch()
    {
        mMap.clear();
        EditText lfs = (EditText) findViewById(R.id.locationFormStreet);
        EditText lfsn = (EditText) findViewById(R.id.locationFormStreetNumber);
        EditText lfc = (EditText) findViewById(R.id.locationFormCity);
        EditText lfz = (EditText) findViewById(R.id.locationFormZip);
        String location = lfs.getText().toString() + " " + lfsn.getText().toString() + " " + lfc.getText().toString() + " " + lfz.getText().toString() + " Bulgaria";
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            lat = address.getLatitude();
            lng = address.getLongitude();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        }
    }

    //DRAWER MENU
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private void onSuccessResponse()
    {
        /*spinnerPosition = (Spinner) findViewById(R.id.userFormPosition);
        adapterPosition = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, displayList);
        adapterPosition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(adapterPosition);

        spinnerAdmin = (Spinner) findViewById(R.id.userFormAdmin);
        adapterAdmin = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, adminList);
        adapterAdmin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdmin.setAdapter(adapterAdmin);*/
    }

    private Long getPositionId(String name)
    {
        for (Position pos : list)
        {
            if (name.equals(pos.getName()))
                return pos.getId();
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}
