package ar.edu.uade.deremateapp.ui.main;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.R;
import ar.edu.uade.deremateapp.data.api.EntregasAPIService;
import ar.edu.uade.deremateapp.data.api.model.EntregasReponseDTO;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class EntregaDetailsFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_ENTREGA = "entregaObj";

    @Inject
    EntregasAPIService entregasAPIService;

    private TextView txDireccion;
    private TextView txEstado;
    private TextView txFechaCreacion;
    private TextView txObservaciones;
    private TextView txComentario;
    private LinearLayout starsContainer;
    private Button btnUpdateStatus;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private EntregasReponseDTO entregaDetails;

    public static EntregaDetailsFragment newInstance(EntregasReponseDTO entrega) {
        EntregaDetailsFragment fragment = new EntregaDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ENTREGA, entrega);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrega_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txDireccion = view.findViewById(R.id.txDireccion);
        txEstado = view.findViewById(R.id.txEstado);
        txFechaCreacion = view.findViewById(R.id.txFechaCreacion);
        txObservaciones = view.findViewById(R.id.txObservaciones);
        txComentario = view.findViewById(R.id.txComentario);
        starsContainer = view.findViewById(R.id.starsContainer);
        btnUpdateStatus = view.findViewById(R.id.btnUpdateStatus);

        if (getArguments() != null) {
            entregaDetails = (EntregasReponseDTO) getArguments().getSerializable(ARG_ENTREGA);
        }

        if (entregaDetails == null) {
            Toast.makeText(requireContext(), "No se pudo cargar la entrega", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        txDireccion.setText(entregaDetails.getDireccion());
        txEstado.setText(entregaDetails.getEstado());
        txFechaCreacion.setText(entregaDetails.getFechaCreacion());
        txObservaciones.setText(entregaDetails.getObservaciones());
        txComentario.setText(entregaDetails.getComentario());

        mostrarEstrellas(entregaDetails.getCalificacion());

        switch (entregaDetails.getEstado()) {
            case "PENDIENTE":
                btnUpdateStatus.setVisibility(VISIBLE);
                btnUpdateStatus.setText("Actualizar: Iniciar viaje");
                break;
            case "EN_VIAJE":
                btnUpdateStatus.setVisibility(VISIBLE);
                btnUpdateStatus.setText("Actualizar: Completar entrega");
                break;
            case "CANCELADO":
            case "ENTREGADO":
                btnUpdateStatus.setVisibility(INVISIBLE);
                View mapContainer = view.findViewById(R.id.map_fragment);
                if (mapContainer != null) mapContainer.setVisibility(INVISIBLE);
                break;
        }

        btnUpdateStatus.setOnClickListener(v -> {
            String nuevoEstado = null;
            switch (entregaDetails.getEstado()) {
                case "PENDIENTE":
                    nuevoEstado = "EN_VIAJE";
                    break;
                case "EN_VIAJE":
                    nuevoEstado = "ENTREGADO";
                    break;
            }
            if (nuevoEstado != null) {
                entregasAPIService.actualizarEstado(entregaDetails.getId(), nuevoEstado)
                        .enqueue(new Callback<EntregasReponseDTO>() {
                            @Override
                            public void onResponse(Call<EntregasReponseDTO> call, Response<EntregasReponseDTO> response) {
                                if (response.isSuccessful()) {
                                    new AlertDialog.Builder(requireContext())
                                            .setTitle("Estado actualizado")
                                            .setMessage("El estado se actualizó correctamente.")
                                            .setPositiveButton("OK", (dialog, which) -> requireActivity().onBackPressed())
                                            .setCancelable(false)
                                            .show();
                                } else {
                                    Toast.makeText(requireContext(), "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<EntregasReponseDTO> call, Throwable t) {
                                Toast.makeText(requireContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void mostrarEstrellas(int calificacion) {
        int[] estrellasIds = {
                R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5
        };
        for (int i = 0; i < estrellasIds.length; i++) {
            ImageView star = requireView().findViewById(estrellasIds[i]);
            if (i < calificacion) {
                star.setImageResource(R.drawable.ic_star_filled);
            } else {
                star.setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
        } else {
            enableUserLocation();
        }

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(entregaDetails.getDireccion(), 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address location = addressList.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                googleMap.addMarker(new MarkerOptions().position(latLng).title(entregaDetails.getDireccion()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && googleMap != null) {
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
                Log.e("Map", "Address not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && googleMap != null) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        }
    }
}
