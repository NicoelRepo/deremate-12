package ar.edu.uade.deremateapp.data.api.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor@NoArgsConstructor
public class LoginResponseDTO {
    private String jwtToken;
}
