package ar.edu.uade.deremateapp.data.api;

import ar.edu.uade.deremateapp.data.api.model.LoginDTO;
import ar.edu.uade.deremateapp.data.api.model.LoginResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginAPIService {

    @POST("auth/login")
    Call<LoginResponseDTO> doLogin(@Body LoginDTO loginDTO);
}
