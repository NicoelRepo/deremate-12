package ar.edu.uade.deremateapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.data.repository.token.TokenRepository;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DebugFragment extends Fragment {

    @Inject
    TokenRepository tokenRepository;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_debug, container, false);

        Button btnCleanToken = view.findViewById(R.id.btnCleanToken);


        btnCleanToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Click debug btn");
                tokenRepository.clearToken();

            }
        });

        return view;
    }
}
