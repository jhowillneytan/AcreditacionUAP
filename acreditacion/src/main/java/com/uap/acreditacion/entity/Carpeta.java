package com.uap.acreditacion.entity;


import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "acre_carpeta")
@Getter
@Setter
public class Carpeta implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_carpeta;

    private String nom_carpeta;

    private String descripcion;
	private String estado;
	private String ruta_icono;
	@Temporal(TemporalType.DATE)
    private Date fecha_registro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

	/*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
*/
    @ManyToOne
    @JoinColumn(name="c_padre", referencedColumnName = "id_carpeta")
    private Carpeta carpetaPadre;

    @OneToMany(mappedBy = "carpetaPadre")
    private List<Carpeta> carpetasHijos;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "carpeta")
    private List<Archivo> archivos;

    /*@JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "carpetas", fetch = FetchType.LAZY)
	private List<Usuario> usuarios;
*/
    /*@ManyToMany(cascade = CascadeType.ALL, mappedBy = "carpetas", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();*/
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="usuario_carpeta",
    joinColumns=@JoinColumn(name = "id_carpeta"),
    inverseJoinColumns = @JoinColumn(name = "id_usuario"))
    private Set<Usuario> usuarios;

}
