package ar.edu.uade.deremateapp.data.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data@AllArgsConstructor
public class LoginDTO {

    private String email;
    private String password;
}
