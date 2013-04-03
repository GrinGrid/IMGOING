package net.gringrid.imgoing;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class MapActivity extends FragmentActivity{

	GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);		
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                //setUpMap();
            }
            //moveThere(37.563387,126.987212);
            moveThere(37.48998628, 126.82505027);
            
        }
    	mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Log.d("Test", "marker.getId() = "+marker.getId());
				return false;
			}
		});
    	
    	// Instantiates a new Polyline object and adds points to define a rectangle
    	PolylineOptions rectOptions = new PolylineOptions()
    	        .add(new LatLng(37.48998628, 126.82505027))
    	        .add(new LatLng(37.48998628, 126.82505027))  // North of the previous point, but at the same longitude
    	        //.add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
    	        //.add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
    	        .add(new LatLng(37.49173843, 126.83110025)); // Closes the polyline.

    	// Get back the mutable Polyline
    	Polyline polyline = mMap.addPolyline(rectOptions);
    	
    	
    	
    	
    }

    private void moveThere(double lat, double lng){
    	LatLng latLng = new LatLng(lat, lng);
    	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
    	mMap.addMarker(new MarkerOptions()
    			.position(latLng)
    			.title("명동성당")    			
    			.snippet("간략한 설명")
    			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))    			
    			);
    }

}
