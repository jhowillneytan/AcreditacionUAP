package com.uap.acreditacion.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EstudianteDto {
    private String ru;
    private String ci;
    private String nombres;
    private String paterno;
    private String materno;
    private String nombreCompleto;
    private String tipoAdminision;
    private String modalidad;
}
