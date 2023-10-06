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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "acre_archivo")
@Getter
@Setter
public class Archivo implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_archivo;

    private String nom_archivo;
    
    private String descripcion;

    private String file;

    private String tipoArchivo;

    private String estado;
    @Temporal(TemporalType.DATE)
    private Date fecha_registro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carpeta")
    private Carpeta carpeta;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="parametro_archivo",
    joinColumns=@JoinColumn(name = "id_archivo"),
    inverseJoinColumns = @JoinColumn(name = "id_parametro"))
    private Set<Parametro> parametros;
}
