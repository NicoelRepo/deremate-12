package ar.edu.uade.deremateapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.R;
import ar.edu.uade.deremateapp.data.api.EntregasAPIService;
import ar.edu.uade.deremateapp.data.api.model.EntregasReponseDTO;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    @Inject
    EntregasAPIService entregasAPIService;

    private ListView listView;
    private List<String> entregasDisplayList;
    private List<EntregasReponseDTO> entregasList;
    private ArrayAdapter<String> adapter;

    private TextView textTotalEntregas;
    private TextView textEntregasCompletadas;
    private TextView textEntregasPendientes;
    private TextView textUltimaEntrega;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listView);
        entregasDisplayList = new ArrayList<>();
        entregasList = new ArrayList<>();
        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, entregasDisplayList);
        listView.setAdapter(adapter);

        textTotalEntregas = view.findViewById(R.id.textTotalEntregas);
        textEntregasCompletadas = view.findViewById(R.id.textEntregasCompletadas);
        textEntregasPendientes = view.findViewById(R.id.textEntregasPendientes);
        textUltimaEntrega = view.findViewById(R.id.textUltimaEntrega);

        listView.setOnItemClickListener((parent, vw, position, id) -> {
            EntregasReponseDTO entregaSeleccionada = entregasList.get(position);
            EntregaDetailsFragment detailsFragment = EntregaDetailsFragment.newInstance(entregaSeleccionada);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        entregasAPIService.obtenerMisEntregas("ENTREGADO,CANCELADO").enqueue(new Callback<List<EntregasReponseDTO>>() {
            @Override
            public void onResponse(Call<List<EntregasReponseDTO>> call, Response<List<EntregasReponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    entregasList.clear();
                    entregasDisplayList.clear();

                    int completadas = 0;
                    int pendientes = 0;
                    EntregasReponseDTO ultimaEntrega = null;

                    for (EntregasReponseDTO entrega : response.body()) {
                        String msg = String.format("Dirección: %s, Estado: %s", entrega.getDireccion(), entrega.getEstado());
                        entregasList.add(entrega);
                        entregasDisplayList.add(msg);

                        if ("ENTREGADO".equalsIgnoreCase(entrega.getEstado())) {
                            completadas++;
                        } else if ("PENDIENTE".equalsIgnoreCase(entrega.getEstado()) || "EN_VIAJE".equalsIgnoreCase(entrega.getEstado())) {
                            pendientes++;
                        }


                        if(entrega.getEstado().equals("ENTREGADO")){
                            if (ultimaEntrega == null || entrega.getFechaCreacion().compareTo(ultimaEntrega.getFechaCreacion()) > 0) {
                                ultimaEntrega = entrega;
                            }
                        }
                    }

                    int total = entregasList.size();

                    int finalCompletadas = completadas;
                    int finalPendientes = pendientes;
                    EntregasReponseDTO finalUltimaEntrega = ultimaEntrega;

                    requireActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        textTotalEntregas.setText(String.valueOf(total));
                        textEntregasCompletadas.setText(String.valueOf(finalCompletadas));
                        textEntregasPendientes.setText(String.valueOf(finalPendientes));
                        if (finalUltimaEntrega != null) {
                            textUltimaEntrega.setText(
                                    String.format("Dirección: %s\nFecha: %s\nEstado: %s",
                                            finalUltimaEntrega.getDireccion(),
                                            finalUltimaEntrega.getFechaCreacion(),
                                            finalUltimaEntrega.getEstado())
                            );
                        } else {
                            textUltimaEntrega.setText("Sin entregas completadas aún");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<EntregasReponseDTO>> call, Throwable t) {
                // Manejo de error opcional
            }
        });
    }
}
