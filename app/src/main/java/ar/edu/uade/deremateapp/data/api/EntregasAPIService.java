package ar.edu.uade.deremateapp.data.api;

import java.util.List;

import ar.edu.uade.deremateapp.data.api.model.EntregasReponseDTO;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EntregasAPIService {

    @GET("api/entregas/mis-entregas")
    Call<List<EntregasReponseDTO>> obtenerMisEntregas();
}
