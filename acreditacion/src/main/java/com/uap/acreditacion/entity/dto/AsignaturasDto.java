package com.uap.acreditacion.entity.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignaturasDto {
    private String sigla;
    private String nombre;
    private String tipoEvaluacion;
    private String grupo;
    private String plan;
    private String calificacion;
    private String gestion;
    private DocenteDto docente;
}
