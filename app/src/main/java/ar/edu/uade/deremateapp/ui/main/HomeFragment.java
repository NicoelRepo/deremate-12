package ar.edu.uade.deremateapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
public class HomeFragment extends Fragment {

    @Inject
    EntregasAPIService entregasAPIService;

    private ListView listView;
    private List<EntregasReponseDTO> entregasList;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listView);
        entregasList = new ArrayList<>();

        entregasAPIService.obtenerPendientes().enqueue(new Callback<List<EntregasReponseDTO>>() {
            @Override
            public void onResponse(Call<List<EntregasReponseDTO>> call, Response<List<EntregasReponseDTO>> response) {
                if (response.isSuccessful()) {
                    entregasList = response.body();
                    listView.setAdapter(new EntregaAdapter(view.getContext(), entregasList));
                }
            }

            @Override
            public void onFailure(Call<List<EntregasReponseDTO>> call, Throwable t) {
            }
        });

        listView.setOnItemClickListener((parent, vw, position, id) -> {
            EntregasReponseDTO entregaSeleccionada = entregasList.get(position);
            EntregaDetailsFragment detailsFragment = EntregaDetailsFragment.newInstance(entregaSeleccionada);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private class EntregaAdapter extends ArrayAdapter<EntregasReponseDTO> {
        private final LayoutInflater inflater;

        public EntregaAdapter(@NonNull android.content.Context context, @NonNull List<EntregasReponseDTO> objects) {
            super(context, 0, objects);
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_entrega, parent, false);
            }

            EntregasReponseDTO entrega = getItem(position);
            TextView txtDireccion = convertView.findViewById(R.id.txtDireccion);
            TextView txtEstado = convertView.findViewById(R.id.txtEstado);
            TextView txtComentario = convertView.findViewById(R.id.txtComentario);
            LinearLayout starsContainer = convertView.findViewById(R.id.starsContainer);

            txtDireccion.setText(entrega.getDireccion());
            txtEstado.setText(entrega.getEstado());
            txtComentario.setText(entrega.getComentario());

            int[] estrellasIds = {
                    R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5
            };
            for (int i = 0; i < estrellasIds.length; i++) {
                ImageView star = convertView.findViewById(estrellasIds[i]);
                if (i < entrega.getCalificacion()) {
                    star.setImageResource(R.drawable.ic_star_filled);
                } else {
                    star.setImageResource(R.drawable.ic_star_empty);
                }
            }

            return convertView;
        }
    }
}
