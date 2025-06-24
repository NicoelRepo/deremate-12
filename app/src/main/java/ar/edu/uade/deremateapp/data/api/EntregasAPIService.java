package ar.edu.uade.deremateapp.data.api;

import java.util.List;

import ar.edu.uade.deremateapp.data.api.model.EntregasReponseDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EntregasAPIService {

    @GET("api/entregas/mis-entregas")
    Call<List<EntregasReponseDTO>> obtenerMisEntregas(@Query("estados") String estados);

    @GET("api/entregas/pendientes")
    Call<List<EntregasReponseDTO>> obtenerPendientes();

    @PUT("api/entregas/{id}/estado")
    Call<EntregasReponseDTO> actualizarEstado(@Path("id") long id, @Query("nuevoEstado") String estado);

}
