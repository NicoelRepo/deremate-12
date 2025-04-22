package ar.edu.uade.deremateapp.data.api;

import ar.edu.uade.deremateapp.data.api.model.UsuarioDTO;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UsuarioService {
    @GET("/api/usuario")
    Call<UsuarioDTO> getUsuario();
}