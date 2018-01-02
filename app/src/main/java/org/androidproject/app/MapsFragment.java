package org.androidproject.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
//import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap = null;
    private MapView mapView;
    LocationManager locationManager;
    LocationListener locationListener;
    List<Address> listAddress = new ArrayList<>();

    String TAG = getClass().getSimpleName();
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private static final String SUPPORT_MAP_BUNDLE_KEY = "MapOptions";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //OnMapsFragmentListener mCallback;

    SupportMapFragment mapFragment = null;
    double mLatitude;//나의 위도
    double mLongtitude;//나의 경도
    ArrayList<MarkerInfo> list = new ArrayList<>();
    ArrayList<FoodBankInfo> foodbank = new ArrayList<>();

    //List<Address> listAddress = null;
    Location mLocation;
    Geocoder geocoder;

    FirebaseDatabase database;
    DatabaseReference myRef;

    Boolean check;
    ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_maps, container, false);

        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment==null){
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        Log.d(TAG, "1111");
        checkLocationPermission(); // android 6.0 이상에서 호출해주어야 합니다.

        Log.d(TAG, "4444");
        //Toast.makeText(getActivity(), "권한 설정 후에는 탭을 한번 더 클릭해주세요",Toast.LENGTH_SHORT).show();
        //푸드뱅크 정보 불러오기
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("foodbank");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;

                //Toast.makeText(getActivity(), "정보불러오기", Toast.LENGTH_SHORT).show();
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {

                    FoodBankInfo foodbankinfo = dataSnapshot2.getValue(FoodBankInfo.class);
                    foodbank.add(foodbankinfo);
                    //Log.d(TAG,"foodbank info title = "+foodbank.get(i++).getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) { // 위치정보 수집이 가능한 환경인지 검사.
            //dialog = ProgressDialog.show(getActivity(),"","위치를 가져오는 중입니다", true);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGPSEnabled || isNetworkEnabled) {
                Log.e("GPS Enable", "true");
                if(checkLocationPermission()==false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("권한 설정 완료")
                            .setMessage("FoodBank탭을 한번 더 터치해 주세요")        // 메세지 설정
                            .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();    // 알림창 띄우기
                }
                final List<String> m_lstProviders = locationManager.getProviders(false);

                //if(check==false) {

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.e("onLocationChanged", "onLocationChanged");
                        Log.e("location", "[" + location.getProvider() + "] (" + location.getLatitude() + "," + location.getLongitude() + ")");


                        /*if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{
                                            android.Manifest.permission.ACCESS_FINE_LOCATION
                                    }, 10
                            );
                            return;
                        }*/

                        locationManager.removeUpdates(locationListener);

                        //위도 경도
                        mLatitude = location.getLatitude();   //위도
                        mLongtitude = location.getLongitude(); //경도

                        //콜백클래스 설정
                        mapFragment.getMapAsync(MapsFragment.this);

                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.e("onStatusChanged", "onStatusChanged");
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.e("onProviderEnabled", "onProviderEnabled");
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.e("onProviderDisabled", "onProviderDisabled");
                    }
                };
                //}


                // QQQ: 시간, 거리를 0 으로 설정하면 가급적 자주 위치 정보가 갱신되지만 베터리 소모가 많을 수 있다.

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (String name : m_lstProviders) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            locationManager.requestLocationUpdates(name, 1000, 0, locationListener);
                        }

                    }
                });


            } else {
                Log.e("GPS Enable", "false");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
            }
        }

        return view;

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("권한 설정 완료")
                    .setMessage("FoodBank탭을 한번 더 터치해 주세요")        // 메세지 설정
                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {

                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();    // 알림창 띄우기*/
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        //dialog.hide();
        Log.d(TAG, "onMapReady()");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("address");
        GeoFire geoFire = new GeoFire(myRef);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        int count = 0;


        final ClusterManager<MarkerInfo> mClusterManager = new ClusterManager<MarkerInfo>(getActivity(), googleMap);
        googleMap.setOnCameraChangeListener(mClusterManager);

        for (int j = 0; j < foodbank.size(); j++) {
            final String name = foodbank.get(j).getName();
            final String phone = foodbank.get(j).getPhone();
            final String address = foodbank.get(j).getLocation();
            geoFire.getLocation(name, new LocationCallback() {
                @Override
                public void onLocationResult(String key, GeoLocation location) {
                    if (location != null) {
                        LatLng position = new LatLng(location.latitude, location.longitude);
                        //Toast.makeText(getActivity(), "위도 : " + position.latitude + "경도 : " + position.longitude, Toast.LENGTH_SHORT).show();

                        //Marker marker3 = googleMap.addMarker(new MarkerOptions().position(position).title(name));
                        mClusterManager.addItem(new MarkerInfo(position, name, phone, address));
                        //Log.d(TAG, "Title is : " + marker3.getTitle());
                        list.add(new MarkerInfo(position, name, phone, address));
                    } else {
                        System.out.println(String.format("There is no location for key %s in GeoFire", key));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("There was an error getting the GeoFire location: " + databaseError);
                }
            });
        }
        //geocoder로 불러오지 못하는 주소 임의로 저장
        LatLng position1 = new LatLng(37.204864, 127.544609);
        mClusterManager.addItem(new MarkerInfo(position1, "여주군좋은이웃기초푸드뱅크", "031-884-1377", "경기도 여주시 가남읍 태평2길 13"));
        //Marker marker1 = googleMap.addMarker(new MarkerOptions().position(position1).title("여주군좋은이웃기초푸드뱅크"));
        list.add(new MarkerInfo(position1, "여주군좋은이웃기초푸드뱅크", "031-884-1377", "경기도 여주시 가남읍 태평2길 13"));

        LatLng position2 = new LatLng(36.720277, 127.431773);
        mClusterManager.addItem(new MarkerInfo(position2, "새영기초푸드뱅크", "043-218-2605", "충청북도 청주시 청원구 오창읍 연구단지로 35"));
        //Marker marker2 = googleMap.addMarker(new MarkerOptions().position(position2).title("새영기초푸드뱅크"));
        list.add(new MarkerInfo(position2, "새영기초푸드뱅크", "043-218-2605", "충청북도 청주시 청원구 오창읍 연구단지로 35"));

        LatLng position3 = new LatLng(36.704207, 127.511978);
        mClusterManager.addItem(new MarkerInfo(position3, "청원기초푸드뱅크", "043-218-1377", "충청북도 청주시 청원구 내수읍 내수로 737"));
        //Marker marker3 = googleMap.addMarker(new MarkerOptions().position(position3).title("청원기초푸드뱅크"));
        list.add(new MarkerInfo(position3, "청원기초푸드뱅크", "043-218-1377", "충청북도 청주시 청원구 내수읍 내수로 737"));

        LatLng position4 = new LatLng(36.516216, 127.467614);
        mClusterManager.addItem(new MarkerInfo(position4, "대청기초푸드뱅크", "043-221-7838", "충청북도 청주시 상당구 문의면 두모1길 13-8"));
        //Marker marker4 = googleMap.addMarker(new MarkerOptions().position(position4).title("대청기초푸드뱅크"));
        list.add(new MarkerInfo(position4, "대청기초푸드뱅크", "043-221-7838", "충청북도 청주시 상당구 문의면 두모1길 13-8"));

        LatLng position5 = new LatLng(34.796526, 126.690597);
        mClusterManager.addItem(new MarkerInfo(position5, "영암군푸드뱅크", "061-471-9933", "전라남도 영암군 영암읍 새동네길 22-1"));
        //Marker marker5 = googleMap.addMarker(new MarkerOptions().position(position5).title("영암군푸드뱅크"));
        list.add(new MarkerInfo(position5, "영암군푸드뱅크", "061-471-9933", "전라남도 영암군 영암읍 새동네길 22-1"));


        final LatLng mPosition = new LatLng(mLatitude,mLongtitude);

        //Marker marker = googleMap.addMarker(new MarkerOptions().position(mPosition).title("내위치").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mClusterManager.addItem(new MarkerInfo(mPosition, "내위치", "", ""));

        list.add(new MarkerInfo(mPosition, "내위치", "", ""));


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mPosition));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        googleMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MarkerInfo>(){

            @Override
            public boolean onClusterClick(Cluster<MarkerInfo> cluster) {
                Toast.makeText(getActivity(), "지도를 확대해 보세요", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MarkerInfo>() {
            @Override
            public boolean onClusterItemClick(MarkerInfo item) {
                if(item.getName().equals("내위치"))
                    Toast.makeText(getActivity(), "현재 고객님이 계신 위치입니다.", Toast.LENGTH_SHORT).show();
                else{
                    for (int i = 0; i < list.size(); i++) {
                        String originalMarker = list.get(i).getName();
                        if (originalMarker.equals(item.getName())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(list.get(i).getName())        // 제목 설정
                                    .setMessage("푸드뱅크 위치 : " +list.get(i).address+"\n" + "푸드뱅크 전화번호 : " + list.get(i).getPhone())        // 메세지 설정
                                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        // 확인 버튼 클릭시 설정
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog dialog = builder.create();    // 알림창 객체 생성
                            dialog.show();    // 알림창 띄우기
                            break;
                        }
                    }
                }
                return false;
            }
        });

        FloatingActionButton myfab = (FloatingActionButton)getActivity().findViewById(R.id.fab_location);
        myfab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getActivity(), "현재 위치로 이동합니다", Toast.LENGTH_SHORT).show();
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mPosition));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        });


    }

}
