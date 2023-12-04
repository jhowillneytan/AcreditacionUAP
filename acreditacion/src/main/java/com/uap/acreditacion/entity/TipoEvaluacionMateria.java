package com.uap.acreditacion.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tipoEvaluacionMateria")
@Getter
@Setter
public class TipoEvaluacionMateria {

     private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_tipoEvaluacionMateria;
    private String nombre;
    private String estado;   

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia")
    private Materia materia;
}
