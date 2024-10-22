package com.uap.acreditacion.entity.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocenteDto {
    private String ci;
    private String rd;
    private String nombre;
    private String paterno;
    private String materno;
    private String nombreCompleto;
    private String gradoAcademico;
    private String direccion;
    private String celular;
    private String gestion;
    private List<String> periodos;
    private List<AsignaturasDto> asignaturasDto;
    private List<String> correos;
}
