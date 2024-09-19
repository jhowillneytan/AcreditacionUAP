package com.uap.acreditacion.entity.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EstadisticaGeneralDto {
    private String cantProgramados;
    private String cantMatriculados;
    private List<EstudianteTituladoDto> listaEstudiantesTitulados;
    private List<AdmisionDto> listaAdmitidos;
    private List<TituladosDto> listaTitulados;
    private List<EstudianteDto> listaEstudiantes;
}
