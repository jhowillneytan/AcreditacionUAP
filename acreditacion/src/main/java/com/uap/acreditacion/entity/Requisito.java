package com.uap.acreditacion.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "requisitos")
@Getter
@Setter
public class Requisito implements Serializable{
    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_requisito;
    private String nombre;
    private String estado;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "requisito")
    private List<Parametro> parametros;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "requisitos", fetch = FetchType.LAZY)
    private Set<Materia> materias = new HashSet<>();

}
