package com.uap.acreditacion.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "tipoEvaluacionMaterias", fetch = FetchType.LAZY)
    private Set<Materia> materias = new HashSet<>();

}
