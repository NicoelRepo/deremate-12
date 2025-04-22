package ar.edu.uade.deremateapp.data.api;

import ar.edu.uade.deremateapp.data.api.model.ConfirmacionRegistroDTO;
import ar.edu.uade.deremateapp.data.api.model.ConfirmarRecoveryDTO;
import ar.edu.uade.deremateapp.data.api.model.UsuarioDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistroAPIService {

    @POST("auth/registro")
    Call<UsuarioDTO> doRegistro(@Body UsuarioDTO dto);

    @POST("auth/confirmar-registro")
    Call<Void> doConfirmarRegistro(@Body ConfirmacionRegistroDTO dto);

    @POST("auth/olvido-password")
    Call<UsuarioDTO> doRecovery(@Body UsuarioDTO dto);

    @POST("auth/confirmar-passwd-recovery")
    Call<Void> doConfirmarRecovery(@Body ConfirmarRecoveryDTO dto);

}
