package ar.edu.uade.deremateapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listView);
        entregasDisplayList = new ArrayList<String>();
        entregasList = new ArrayList<EntregasReponseDTO>();
        adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, entregasDisplayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, vw, position, id) -> {
            Intent intent = new Intent(getActivity(), EntregaDetailsActivity.class);

            intent.putExtra("entregaObj",entregasList.get(position));

            startActivity(intent);
        });

        entregasAPIService.obtenerMisEntregas().enqueue(new Callback<List<EntregasReponseDTO>>() {
            @Override
            public void onResponse(Call<List<EntregasReponseDTO>> call, Response<List<EntregasReponseDTO>> response) {
                if(response.isSuccessful()){
                    for(EntregasReponseDTO entrega: response.body()){
                        var msg = String.format("Direccion: %s, Estado: %s", entrega.getDireccion(), entrega.getEstado());
                        entregasList.add(entrega);
                        entregasDisplayList.add(msg);
                    }
                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            }

            @Override
            public void onFailure(Call<List<EntregasReponseDTO>> call, Throwable t) {

            }
        });

    }

}
