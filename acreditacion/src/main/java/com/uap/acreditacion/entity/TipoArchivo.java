package com.uap.acreditacion.entity;


import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "acre_tipo_archivo")
@Getter
@Setter
public class TipoArchivo implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_tipo_archivo;

    private String nom_tipo_archivo;

    private String icono;

    private String estado;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "tipoArchivo")
    private List<Archivo> archivos;

	public Long getId_tipo_archivo() {
		return id_tipo_archivo;
	}

	public void setId_tipo_archivo(Long id_tipo_archivo) {
		this.id_tipo_archivo = id_tipo_archivo;
	}

	public String getNom_tipo_archivo() {
		return nom_tipo_archivo;
	}

	public void setNom_tipo_archivo(String nom_tipo_archivo) {
		this.nom_tipo_archivo = nom_tipo_archivo;
	}

	/*public String getIcono() {
		return icono;
	}

	public void setIcono(String icono) {
		this.icono = icono;
	}*/

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<Archivo> getArchivos() {
		return archivos;
	}

	public void setArchivos(List<Archivo> archivos) {
		this.archivos = archivos;
	}
    
}
