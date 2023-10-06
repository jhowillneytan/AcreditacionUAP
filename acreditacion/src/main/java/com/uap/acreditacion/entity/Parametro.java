package com.uap.acreditacion.entity;

import java.io.Serializable;
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
@Table(name = "parametro")
@Getter
@Setter
public class Parametro implements Serializable{
    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_parametro;
    private String nombre;
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_requisito")
    private Requisito requisito;

    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "parametros")
    private List<Archivo> archivos;

}
