package ar.edu.uade.deremateapp.data.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data@AllArgsConstructor
public class ConfirmarRecoveryDTO {

    private String email;
    private String codigo;
    private String password;
}
