package com.uap.acreditacion.entity;


import java.io.Serializable;
import java.util.Date;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
public class Usuario implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    private String username;
    private String password;

    private String permisosCarpeta;
    private String estado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Temporal(TemporalType.DATE)
    private Date fecha_registro;

   // @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "usuario")
   // private List<Carpeta> carpetas = new HashSet<>();
 
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "usuarios", fetch = FetchType.LAZY)
    private Set<Carpeta> carpetas = new HashSet<>();
   /*  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="usuario_carpeta",
    joinColumns=@JoinColumn(name = "id_usuario"),
    inverseJoinColumns = @JoinColumn(name = "id_carpeta"))
    private Set<Carpeta> carpetas;*/

    /* @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "usuarios2", fetch = FetchType.LAZY)
	private List<Carpeta> carpetas2;*/
}
