package ar.edu.uade.deremateapp.data.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfirmacionRegistroDTO {
    private String email;
    private String codigo;
}

