package com.cybermed.cdoc_patient.maps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.VectorFavoritePharmacy;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.FavoritePharmacy;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseActivity;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.libraries.places.api.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, PlacesAutoCompleteAdapter.ClickListener {

    private GoogleMap mMap;
    private MeFragment meFragment;

    private static final String LOG_TAG = "MainFragment";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private EditText mAutocompleteTextView;
    //private EditText mAutocompleteTextView;
    private TextView searchBtn;
    private Button favoriteBtn;
    private Button pharmacyListBtn;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private String vicinity;

    double currLatitude;
    double currLongitude;
    double currLatitude2;
    double currLongitude2;
    private String user_id;

    private AsyncTask mGetFavoritePharmaciesTask;
    private AsyncTask mSetFavoritePharmaciesTask;
    private Location location;

    private MapPharmacyAdapter adapter;
    private ListView listView;
    private RelativeLayout pharmacyListLayout;

    private List<FavoritePharmacy> favoritePharmaciesList;

    private boolean badDevices = false;

    private ImageView emptyImage;
    private TextView emptyTxt;
    RecyclerView mSearchRecyclerView;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private Polyline currentPolyline;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View mapView = mapFragment.getView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(Color.WHITE);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.my_pharmacies_heading));
        //fragMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initLocationBtn(mapView);

        searchBtn = (TextView) findViewById(R.id.searchBtn);
        favoriteBtn = (Button) findViewById(R.id.favoriteBtn);
        pharmacyListBtn = (Button) findViewById(R.id.pharmacyListBtn);
        pharmacyListLayout = (RelativeLayout) findViewById(R.id.pharmacyListLayout);
        favoritePharmaciesList = new ArrayList<>();
        emptyImage = (ImageView) findViewById(R.id.emptypharmacy);
        emptyTxt = (TextView) findViewById(R.id.emptypharmacytxt);
        listView = (ListView) findViewById(R.id.listView);
        mSearchRecyclerView = findViewById(R.id.places_recycler_view);

        adapter = new MapPharmacyAdapter(this);
        listView.setAdapter(adapter);

        GetFavoritePharmacies(user_id);


        pharmacyListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pharmacyListBtn.isSelected()) {
                    pharmacyListBtn.setSelected(true);
                    favoriteBtn.setVisibility(View.GONE);
                    pharmacyListLayout.setVisibility(View.VISIBLE);
                } else {
                    pharmacyListBtn.setSelected(false);
                    pharmacyListLayout.setVisibility(View.GONE);
                }
            }
        });
        /*View mapToolbar = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("4"));

        // and next place it, for example, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
*/
//        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
//                .addApi(Places.GEO_DATA_API)
//                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
//                .addConnectionCallbacks(this)
//                .build();


        mAutocompleteTextView = findViewById(R.id.autoCompleteTextView);
//        mAutocompleteTextView.setThreshold(2);
//
//        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
//        AutocompleteFilter filter = new AutocompleteFilter.Builder()
//                .setCountry("US")
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
//                .build();
//
//        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW, filter);
//        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
//
//        mAutocompleteTextView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;
//
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (event.getRawX() >= (mAutocompleteTextView.getRight() - mAutocompleteTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                        mAutocompleteTextView.setText(mAutocompleteTextView.getText());
//
//                        mAutocompleteTextView.cancelLongPress();
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(MapsActivity.this);
                pd.setMessage(getString(R.string.doclist_loading));
                pd.show();
                favoriteBtn.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadPharmacies();
                    }
                }, 2000);

                mAutocompleteTextView.setText("");
                mAutocompleteTextView.clearFocus();
                if (v.getWindowToken() != null) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MapsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MapsActivity.this);
                }
                builder.setTitle(getString(R.string.delete_pharmacies_title))
                        .setMessage(getString(R.string.delete_pharmacies_confirm))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                FavoritePharmacy selectedPharmacy = (FavoritePharmacy) listView.getItemAtPosition(pos);
                                SetFavoritePharmacies(user_id, selectedPharmacy.pharmacy_name, String.valueOf(selectedPharmacy.longitude), String.valueOf(selectedPharmacy.latitude), "", "", "", "", "", "", "", "0");

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });
        mAutocompleteTextView.addTextChangedListener(filterTextWatcher);
        Places.initialize(this, getResources().getString(R.string.google_maps_key));
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        mSearchRecyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();
    }

    private void initLocationBtn(View mapView) {
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right top
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 250, 50, 0);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            currLongitude = loc.longitude;
            currLatitude = loc.latitude;
            currLongitude2 = loc.longitude;
            currLatitude2 = loc.latitude;
            searchLocation(currLatitude, currLongitude);
            //loadPharmacies(); //doesn't work
            mMap.setOnMyLocationChangeListener(null);

        }
    };

    private void searchLocation(double currLatitude, double currLongitude) {
        LatLng coordinate = new LatLng(currLatitude, currLongitude);
        Marker marker =mMap.addMarker(new MarkerOptions().position(coordinate).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_marker)));
        marker.setTitle("My Location");
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coordinate)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                //.bearing(4.title("Marker in Sydney")5)                // Sets the orientation of the camera to east
                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(20, 0, 0, 0);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        //provider = null;
        if (provider != null && locationManager.isProviderEnabled(provider)) {
            location = locationManager.getLastKnownLocation(provider);
        } else {
            Toast.makeText(this, "No location provider on this device", Toast.LENGTH_LONG).show();
            finish();
        }
        pd = new ProgressDialog(MapsActivity.this);
        pd.setMessage(getString(R.string.doclist_loading));
        pd.show();
        if (location != null) {
            Log.d("mapstest", "currentlocation");
            currLatitude = location.getLatitude();
            currLongitude = location.getLongitude();
            currLatitude2 = location.getLatitude();
            currLongitude2 = location.getLongitude();

            searchLocation(currLatitude, currLongitude);
        } else {
            //Workaround for some devices
            badDevices = true;
            mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPharmacies();
            }
        }, 2000);


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                favoriteBtn.setVisibility(View.GONE);
                pharmacyListBtn.setSelected(false);
                pharmacyListLayout.setVisibility(View.GONE);
                //android.util.Log.i("onMapClick", "Horray!");
            }
        });

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

                //use your current location here
                currLatitude = mMap.getCameraPosition().target.latitude;
                currLongitude = mMap.getCameraPosition().target.longitude;

                /*Log.i("MapsCenter", String.valueOf(mMap.getCameraPosition().target.latitude));
                Log.i("MapsCenter", String.valueOf(mMap.getCameraPosition().target.longitude));*/
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (marker.getTitle() != null && marker.getTitle().equals("My Location")) {
                    return true;
                }
                /*Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                /*if (addresses != null) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String zipcode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    //mAutocompleteTextView.setText(city + ", " + state + ", " + country);
                    //mAutocompleteTextView.clearFocus();
                }*/
                if (checkPharmacyisFavorite(marker)) {
                    favoriteBtn.setSelected(true);
                } else {
                    favoriteBtn.setSelected(false);
                }
                //to show favorite button uncomment this line
                //favoriteBtn.setVisibility(View.VISIBLE);
                favoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (favoriteBtn.isSelected()) {
                                favoriteBtn.setSelected(false);
                                removeFavoritePharmacy(marker);
                            } else {
                                favoriteBtn.setSelected(true);
                                addFavoritePharmacy(marker);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                marker.showInfoWindow();

                return false;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
               /* LatLng origin=new LatLng(currLatitude2,currLongitude2);
                LatLng destination=new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);*/

                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null) {
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();

                    LatLng destination=new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
                    openRouteInGoogleMaps( marker.getTitle()+", "+city+", "+state,destination);

                }else {

                    LatLng destination=new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
                    openRouteInGoogleMaps( marker.getTitle()+", "+marker.getSnippet(),destination);
                }
                //openRouteInGoogleMaps( origin,destination);
               // getRoute( origin,  destination);
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {


                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

               /* LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);*/

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.HORIZONTAL); // Set horizontal orientation to align items side by side

// Create an ImageView for the icon
                ImageView infoIcon = new ImageView(context);
                infoIcon.setImageResource(R.drawable.fluent_info_16_regular);// Replace with your actual drawable resource
                int color = ContextCompat.getColor(context, R.color.azure);
                infoIcon.setImageTintList(ColorStateList.valueOf(color));
                infoIcon.setPadding(10, 10, 10, 10); // Add some padding around the icon

// Create a LinearLayout for the TextViews (to keep them vertically aligned)
                LinearLayout textContainer = new LinearLayout(context);
                textContainer.setOrientation(LinearLayout.VERTICAL);

// Create a TextView for the title
                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

// Create a TextView for the snippet
                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

// Add the TextViews to the text container
                textContainer.addView(title);
                textContainer.addView(snippet);

// Add the ImageView and text container to the main LinearLayout

                info.addView(textContainer);
                info.addView(infoIcon);
                return info;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                pharmacyListLayout.setVisibility(View.GONE);
                pharmacyListBtn.setSelected(false);
//                mMap.clear();
                FavoritePharmacy selectedPharmacy = (FavoritePharmacy) listView.getItemAtPosition(position);
                if (selectedPharmacy.latitude != null) {
                    double lat = Double.parseDouble(selectedPharmacy.latitude);
                    double lng = Double.parseDouble(selectedPharmacy.longitude);
                    searchLocation(lat, lng);

                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(selectedPharmacy.pharmacy_name)
                            .snippet(vicinity)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_marker));
                    mMap.addMarker(markerOptions);
                    favoriteBtn.setSelected(true);
                }
            }
        });
    }

    private Boolean checkListNull() {
        if (favoritePharmaciesList != null) {
            for (FavoritePharmacy fP : favoritePharmaciesList)
                if (fP.longitude != null) return true;
        }

        return false;
    }

    private boolean checkPharmacyisFavorite(Marker marker) {
        if (checkListNull()) {
            for (int i = 0; i < favoritePharmaciesList.size(); i++) {
                if (Double.parseDouble(favoritePharmaciesList.get(i).latitude) == marker.getPosition().latitude &&
                        Double.parseDouble(favoritePharmaciesList.get(i).longitude) == marker.getPosition().longitude) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeFavoritePharmacy(Marker marker) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

     //   String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String zipcode = addresses.get(0).getPostalCode();
       // String knownName = addresses.get(0).getFeatureName();


        marker.setTitle(marker.getTitle());
        marker.setSnippet("\n" + vicinity + "\n" + city + ", " + state + " " + zipcode + " " + country);
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        SetFavoritePharmacies(user_id, marker.getTitle(), String.valueOf(marker.getPosition().longitude), String.valueOf(marker.getPosition().latitude), vicinity, "", city, state, country, zipcode, "", "0");

        Toast.makeText(getApplicationContext(), "Pharmacy removed: " + marker.getTitle(), Toast.LENGTH_SHORT).show();

    }

    private void addFavoritePharmacy(Marker marker) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

     //   String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String zipcode = addresses.get(0).getPostalCode();
   //     String knownName = addresses.get(0).getFeatureName();

        marker.setTitle(marker.getTitle());
        marker.setSnippet("\n" + vicinity + "\n" + city + ", " + state + " " + zipcode + " " + country);
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

        SetFavoritePharmacies(user_id, marker.getTitle(), String.valueOf(marker.getPosition().longitude), String.valueOf(marker.getPosition().latitude), vicinity, "", city, state, country, zipcode, "", "1");
        Toast.makeText(getApplicationContext(), getString(R.string.pharmacy_added) + marker.getTitle(), Toast.LENGTH_SHORT).show();

    }

    private void loadPharmacies() {
        StringBuilder sbValue = new StringBuilder(sbMethod());
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(sbValue.toString());
        pd.dismiss();
    }

    public StringBuilder sbMethod() {

        if (badDevices) {
            currLatitude = mMap.getCameraPosition().target.latitude;
            currLongitude = mMap.getCameraPosition().target.longitude;
        }

        Log.d("Maps", "Lat:" + currLatitude + ",Long:" + currLongitude);
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();


        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + currLatitude + "," + currLongitude);
        // Will hit quota
        sb.append("&radius=" + calculateRadius());
        //sb.append("&radius=1500");
        sb.append("&types=" + "pharmacy");
        sb.append("&sensor=true");
        sb.append("&key=" + getResources().getString(R.string.google_maps_key)/*"AIzaSyCKDgjkFvOkKut0CCkqBIEORnHuJcwjvc8"*/);

        Log.d("Map", "api: " + sb.toString());

        return sb;
    }


    private String calculateRadius() {

        VisibleRegion vr = mMap.getProjection().getVisibleRegion();
        double left = vr.latLngBounds.southwest.longitude;
        double bottom = vr.latLngBounds.southwest.latitude;

        Location bottomLeftCornerLocation = new Location("center");//(center's latitude,vr.latLngBounds.southwest.longitude)
        bottomLeftCornerLocation.setLatitude(bottom);
        bottomLeftCornerLocation.setLongitude(left);

        Location center = new Location("center");
        center.setLatitude(vr.latLngBounds.getCenter().latitude);
        center.setLongitude(vr.latLngBounds.getCenter().longitude);
        float dis = center.distanceTo(bottomLeftCornerLocation);//calculate distane between middleLeftcorner and center
        Log.d("MapsRadius", String.valueOf(Math.round(dis)));
        return String.valueOf(Math.round(dis));
    }


    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParserTask
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                Log.d("googlemaps", line);
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception dling url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_JSON placeJson = new Place_JSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJson.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            Log.d("Map", "list size: " + list.size());
            // Clears all the existing markers;
//            mMap.clear();

            for (int i = 0; i < list.size(); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);


                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));
                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));
                // Getting name
                String name = hmPlace.get("place_name");

                // Getting vicinity
                vicinity = hmPlace.get("vicinity");
                //String CurrentString = "Fruit: they taste good";
                String[] separated = vicinity.split(",");
                vicinity = separated[0]; // this will contain "Fruit"
                //separated[1];
                //vicinity = vicinity.substring(0, vicinity.indexOf(','));


                LatLng latLng = new LatLng(lat, lng);

                // Settimng the position for the marker
                markerOptions.position(latLng);

                Log.d("testinggeocoder4", "time");
                markerOptions.title(name)
                        .snippet(vicinity)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pharmacy_marker));

                // Placing a marker on the touched position
                Marker m = mMap.addMarker(markerOptions);
            }

        }
    }

    public class Place_JSON {

        /**
         * Receives a JSONObject and returns a list
         */
        public List<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray jPlaces = null;
            try {
                /** Retrieves all the elements in the 'places' array */
                jPlaces = jObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /** Invoking getPlaces with the array of json object
             * where each json object represent a place
             */
            return getPlaces(jPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> place = null;

            /** Taking each place, parses and adds to list object */
            for (int i = 0; i < placesCount; i++) {
                try {
                    /** Call getPlace with place JSON object to parse the place */
                    place = getPlace((JSONObject) jPlaces.get(i));
                    placesList.add(place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        /**
         * Parsing the Place JSON object
         */
        private HashMap<String, String> getPlace(JSONObject jPlace) {

            HashMap<String, String> place = new HashMap<String, String>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                // Extracting Place name, if available
                if (!jPlace.isNull("name")) {
                    placeName = jPlace.getString("name");
                }

                // Extracting Place Vicinity, if available
                if (!jPlace.isNull("vicinity")) {
                    vicinity = jPlace.getString("vicinity");
                    Log.d("mapsjplace", jPlace.toString());
                }

                latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = jPlace.getString("reference");

                place.put("place_name", placeName);
                place.put("vicinity", vicinity);
                place.put("lat", latitude);
                place.put("lng", longitude);
                place.put("reference", reference);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }

//    private AdapterView.OnItemClickListener mAutocompleteClickListener
//            = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
//            final String placeId = String.valueOf(item.placeId);
//            Log.i(LOG_TAG, "Selected: " + item.description);
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(mGoogleApiClient, placeId);
//            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
//        }
//    };

//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
//            = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()) {
//                Log.e(LOG_TAG, "Place query did not complete. Error: " +
//                        places.getStatus().toString());
//                return;
//            }
//            // Selecting the first object buffer.
//            final Place place = places.get(0);
//            CharSequence attributions = places.getAttributions();
//            Log.d("autocompletetest", String.valueOf(place.getLatLng()));
//
//            /*if (location != null) {
//                //currLatitude = location.getLatitude();
//                //currLongitude = location.getLongitude();
//                LatLng coordinate = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
//                CameraPosition cameraPosition = new CameraPosition.Builder()
//                        .target(coordinate)      // Sets the center of the map to Mountain View
//                        .zoom(14)                   // Sets the zoom
//                        //.bearing(45)                // Sets the orientation of the camera to east
//                        //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
//                        .build();                   // Creates a CameraPosition from the builder
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }*/
//            searchLocation(place.getLatLng().latitude, place.getLatLng().longitude);
//
//
//            mMap.clear();
//
//            // Creating a marker
//            MarkerOptions markerOptions = new MarkerOptions();
//
//            // Getting a place from the places list
//            //HashMap<String, String> hmPlace = list.get(i);
//
//
//            // Getting latitude of the place
//            double lat = place.getLatLng().latitude;
//            currLatitude = lat;
//            // Getting longitude of the place
//            double lng = place.getLatLng().longitude;
//            currLongitude = lng;
//
//            // Getting name
//            String name = place.getName().toString();
//
//            Log.d("Map", "place: " + name);
//
//            // Getting vicinity
//            //String vicinity = hmPlace.get("vicinity");
//
//            LatLng latLng = new LatLng(lat, lng);
//
//            // Setting the position for the marker
//            markerOptions.position(latLng);
//
//            markerOptions.title(name);
//
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//
//            // Placing a marker on the touched position
//            Marker m = mMap.addMarker(markerOptions);
//
//            loadPharmacies();
//
//        }
//    };

    private void GetFavoritePharmacies(String user_id) {
        if (mGetFavoritePharmaciesTask == null) {
            mGetFavoritePharmaciesTask = GetFavoritePharmaciesTask(user_id);
        }
    }

    private void SetFavoritePharmacies(String user_id, String pharmacy_name, String longitude, String latitude, String address1, String address2, String city
            , String state, String country, String zip_code, String type, String Set_as_favorite) {
        if (mSetFavoritePharmaciesTask == null) {
            mSetFavoritePharmaciesTask = SetFavoritePharmaciesTask(user_id, pharmacy_name, longitude, latitude, address1, address2, city, state, country, zip_code, type, Set_as_favorite);
        }
    }


    private AsyncTask SetFavoritePharmaciesTask(final String user_id, final String pharmacy_name, final String longitude, final String latitude, final String address1, final String address2, final String city
            , final String state, final String country, final String zip_code, final String type, final String Set_as_favorite) {

        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Integer doInBackground(Void... params) {

                try {
                    return WebService.getInstance().SetPatientFavoritePharmacy(user_id, pharmacy_name, longitude, latitude, address1, address2, city, state, country, zip_code, type, Set_as_favorite);

                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                mSetFavoritePharmaciesTask = null;

                if (integer == 1) {

                    Log.d("favoritepharmacy", "setfavorite");
                    GetFavoritePharmacies(user_id);
                }

            }
        }.execute();

    }


    private AsyncTask GetFavoritePharmaciesTask(final String user_id) {
        return new AsyncTask<Void, Void, VectorFavoritePharmacy>() {
            Exception e;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected VectorFavoritePharmacy doInBackground(Void... params) {
                try {
                    return WebService.getInstance().GetFavoritePharmarcies(user_id);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(VectorFavoritePharmacy favoritePharmacies) {
                super.onPostExecute(favoritePharmacies);
                mGetFavoritePharmaciesTask = null;
                if (favoritePharmacies != null) {
                    Log.d("favoritepharmacy", "getfavorite");
                    favoritePharmaciesList.clear();
                    favoritePharmaciesList.addAll(favoritePharmacies);
                    adapter.appendList(favoritePharmacies);

                    Log.d("favoritepharmacy", String.valueOf(listView.getCount()));

                    if (listView.getCount() != 0 && ((FavoritePharmacy) listView.getItemAtPosition(0)).longitude != null) {
                        listView.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.GONE);
                        emptyTxt.setVisibility(View.GONE);
                    } else {
                        listView.setVisibility(View.GONE);
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyTxt.setVisibility(View.VISIBLE);
                    }
                }
            }
        }.execute();
    }


    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, getString(R.string.google_map_api_error)
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                getString(R.string.google_map_api_error) +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (mSearchRecyclerView.getVisibility() == View.GONE) {
                    mSearchRecyclerView.setVisibility(View.VISIBLE);
                }
                if (mAutoCompleteAdapter.getItemCount()>0){
                    mSearchRecyclerView.setVisibility(View.VISIBLE);
                }else {
                    mSearchRecyclerView.setVisibility(View.GONE);
                }
            } else {
                if (mSearchRecyclerView.getVisibility() == View.VISIBLE) {
                    mSearchRecyclerView.setVisibility(View.GONE);
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    @Override
    public void click(com.google.android.libraries.places.api.model.Place place) {
        mSearchRecyclerView.setVisibility(View.GONE);
        pd = new ProgressDialog(MapsActivity.this);
        pd.setMessage(getString(R.string.doclist_loading));
        pd.show();
        //Toast.makeText(this, place.getAddress() + ", " + place.getLatLng().latitude + place.getLatLng().longitude, Toast.LENGTH_SHORT).show();

//        if (!place..isSuccess()) {
//            Log.e(LOG_TAG, "Place query did not complete. Error: " +
//                    places.getStatus().toString());
//            return;
//        }
        // Selecting the first object buffer.
        // final Place place = places.get(0);
        //CharSequence attributions = place.getAttributions();
        Log.d("autocompletetest", String.valueOf(place.getLatLng()));

            /*if (location != null) {
                //currLatitude = location.getLatitude();
                //currLongitude = location.getLongitude();
                LatLng coordinate = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(coordinate)      // Sets the center of the map to Mountain View
                        .zoom(14)                   // Sets the zoom
                        //.bearing(45)                // Sets the orientation of the camera to east
                        //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }*/
        searchLocation(place.getLatLng().latitude, place.getLatLng().longitude);


//        mMap.clear();

        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Getting a place from the places list
        //HashMap<String, String> hmPlace = list.get(i);


        // Getting latitude of the place
        double lat = place.getLatLng().latitude;
        currLatitude = lat;
        // Getting longitude of the place
        double lng = place.getLatLng().longitude;
        currLongitude = lng;

        // Getting name
        String name = place.getName().toString();


        Log.d("Map", "place: " + name);

        // Getting vicinity
        //String vicinity = hmPlace.get("vicinity");

        LatLng latLng = new LatLng(lat, lng);

        // Setting the position for the marker
        markerOptions.position(latLng);

        markerOptions.title(name);

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_marker));

        // Placing a marker on the touched position
        Marker m = mMap.addMarker(markerOptions);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPharmacies();
            }
        }, 2000);
        //loadPharmacies();
    }

    public void openRouteInGoogleMaps(String destinationName, LatLng destination) {
        // Create a Uri for the route, specifying the origin and destinationLatLng origin, LatLng destination
       /* String uri = "https://www.google.com/maps/dir/?api=1" +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&travelmode=driving";*/  // Options: driving, walking, bicycling, transit
        if (!TextUtils.isEmpty(destinationName)&&!destinationName.contains("null")){
            String uri = "https://www.google.com/maps/dir/?api=1" +
                    "&destination=" + Uri.encode(destinationName) +
                    "&travelmode=driving";
            // Create an intent to launch Google Maps
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");

            // Check if Google Maps is installed on the device
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Handle the case where Google Maps is not installed
                Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
            }
        }else {
            String uri = "https://www.google.com/maps/dir/?api=1" +
                    "&destination=" + destination.latitude + "," + destination.longitude +
                    "&travelmode=driving";
            // Create an intent to launch Google Maps
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");

            // Check if Google Maps is installed on the device
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Handle the case where Google Maps is not installed
                Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        // Add polyline options here to draw the route
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(origin)
                .add(destination)
                .width(10)
                .color(Color.BLUE);
        mMap.addPolyline(polylineOptions);
    }

    private void getRoute(LatLng origin, LatLng destination) {
        String apiKey ="AIzaSyCKDgjkFvOkKut0CCkqBIEORnHuJcwjvc8";/* "YOUR_GOOGLE_MAPS_API_KEY";*/
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL directionUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) directionUrl.openConnection();
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    String response = sb.toString();
                    // Parse the response and draw the route
                    runOnUiThread(() -> drawRoute(response));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void drawRoute(String jsonResponse) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);
            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
            String encodedString = overviewPolyline.getString("points");

            List<LatLng> routePoints = decodePolyline(encodedString);

            // Draw the polyline on the map
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(routePoints)
                    .width(10)
                    .color(Color.BLUE);
          //  mMap.addPolyline(polylineOptions);
            currentPolyline = mMap.addPolyline(polylineOptions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((lat / 1E5)), ((lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}

