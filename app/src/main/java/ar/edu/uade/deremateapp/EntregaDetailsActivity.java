package ar.edu.uade.deremateapp;

import static android.view.View.INVISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.data.api.EntregasAPIService;
import ar.edu.uade.deremateapp.data.api.model.EntregasReponseDTO;
import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class EntregaDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Inject
    EntregasAPIService entregasAPIService;

    private TextView txDireccion;
    private TextView txEstado;
    private TextView txFechaCreacion;
    private TextView txObservaciones;

    GoogleMap googleMap;
    FusedLocationProviderClient fusedLocationClient;

    EntregasReponseDTO entregaDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrega_details);

        txDireccion = findViewById(R.id.txDireccion);
        txEstado = findViewById(R.id.txEstado);
        txFechaCreacion = findViewById(R.id.txFechaCreacion);
        txObservaciones = findViewById(R.id.txObservaciones);

        entregaDetails = getIntent().getSerializableExtra("entregaObj", EntregasReponseDTO.class);

        txDireccion.setText(entregaDetails.getDireccion());
        txEstado.setText(entregaDetails.getEstado());
        txFechaCreacion.setText(entregaDetails.getFechaCreacion());
        txObservaciones.setText(entregaDetails.getObservaciones());


        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        if(entregaDetails.getEstado().equals("CANCELADO") || entregaDetails.getEstado().equals("ENTREGADO")){
            FragmentContainerView containerView = findViewById(R.id.map_fragment);
            containerView.setVisibility(INVISIBLE);
        }
    }


    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1); // Any request code
        }else {
            enableUserLocation();
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try{

            List<Address> addressList = geocoder.getFromLocationName(entregaDetails.getDireccion(), 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address location = addressList.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                googleMap.addMarker(new MarkerOptions().position(latLng).title(entregaDetails.getDireccion()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && googleMap != null) {
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
                Log.e("Map", "Address not found");
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && googleMap != null) {
            googleMap.setMyLocationEnabled(true);

//            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
//                if (location != null) {
//                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    googleMap.addMarker(new MarkerOptions().position(userLatLng).title("You are here"));
//                    // Optionally move camera to user location
//                     googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
//                }
//            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        }
    }





}
