package ar.edu.uade.deremateapp.data.api.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EntregasReponseDTO {
    private Long id;
    private String direccion;
    private String estado;
    private String fechaEntrega;
    private String fechaCreacion;
    private String observaciones;
    private Long usuarioId;
}
