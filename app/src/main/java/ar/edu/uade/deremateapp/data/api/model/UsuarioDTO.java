package ar.edu.uade.deremateapp.data.api.model;

import lombok.Data;

@Data
public class UsuarioDTO {
    private long id;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private int documento;
}
