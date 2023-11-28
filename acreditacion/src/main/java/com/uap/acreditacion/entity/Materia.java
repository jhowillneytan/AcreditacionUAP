package com.uap.acreditacion.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "materia")
@Getter
@Setter
public class Materia implements Serializable{

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_materia;
    private String nombre;
    private String sigla;
    private String estado;
    private String plan;
    private String evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carpeta")
    private Carpeta carpeta;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="requisito_materia",
    joinColumns=@JoinColumn(name = "id_materia"),
    inverseJoinColumns = @JoinColumn(name = "id_requisito"))
    private Set<Requisito> requisitos;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "asignaturas", fetch = FetchType.LAZY)
    private Set<Estudiante> estudiantes = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "materia")
    private List<Archivo> archivos;

}
