package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Facultad;

public interface IFacultadDao extends CrudRepository<Facultad,Long>{

    @Query(value = "SELECT * FROM acre_facultad af WHERE af.nom_facultad = ?1 LIMIT 1", nativeQuery = true)
    public Facultad facultadPorNombre(String nombreFacultad);

}
