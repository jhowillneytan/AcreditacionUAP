package com.uap.acreditacion.entity;


import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "acre_docente")
@Getter
@Setter
public class Docente implements Serializable{
    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_docente;

    private String rd;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "docente")
    private List<Carpeta> carpetas;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    

}
