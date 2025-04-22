package ar.edu.uade.deremateapp.data.api;

import ar.edu.uade.deremateapp.data.api.model.ConfirmacionRegistroDTO;
import ar.edu.uade.deremateapp.data.api.model.UsuarioDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistroAPIService {

    @POST("auth/registro")
    Call<UsuarioDTO> doRegistro(@Body UsuarioDTO dto);

    @POST("auth/confirmar-registro")
    Call<Void> doConfirmarRegistro(@Body ConfirmacionRegistroDTO dto);

}
