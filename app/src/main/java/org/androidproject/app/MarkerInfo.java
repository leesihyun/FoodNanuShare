package org.androidproject.app;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by seahyun on 2017-05-25.
 */

public class MarkerInfo implements ClusterItem {

    //Marker marker;
    LatLng location;
    String name;
    String phone;
    String address;

    public MarkerInfo(LatLng location, String name, String phone, String address){
        //this.marker = marker;
        this.location = location;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    //public Marker getMarker(){return this.marker;}
    //public void setMarker(){this.marker = marker;}
    public LatLng getLocation(){return this.location;}
    public void setLocation(){this.location = location;}
    public String getName(){return this.name;}
    public void setName(){this.name = name;}
    public String getPhone(){return this.phone;}
    public void setPhone(){this.phone = phone;}
    public String getAddress(){return this.address;}
    public void setAddress(){this.address = address;}

    @Override
    public LatLng getPosition() {
        return location;
    }
}
