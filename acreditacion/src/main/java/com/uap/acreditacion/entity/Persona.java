package com.uap.acreditacion.entity;


import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "acre_persona")
@Getter
@Setter
public class Persona implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_persona;

    private String nombre;

    private String ap_paterno;

    private String ap_materno;

    private String email;

    private String ci;

    private String imagen_persona;

    private String estado;
    @Temporal(TemporalType.DATE)
    private Date fecha_registro;

	@OneToOne(mappedBy = "persona", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Usuario usuario;

   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_persona")
    private TipoPersona tipoPersona;

	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cargo")
    private Cargo cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_puesto")
    private Puesto puesto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

}
