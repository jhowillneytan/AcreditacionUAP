package com.uap.acreditacion.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "acre_facultad")
@Getter
@Setter
public class Facultad implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_facultad;

    private String nom_facultad;

    private String descripcion;

    private String estado;
    @Temporal(TemporalType.DATE)
    private Date fecha_registro;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facultad")
    private List<Carrera> carreras;

}
