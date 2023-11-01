package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Carrera;

public interface ICarreraDao extends CrudRepository<Carrera,Long>{
    @Query(value = "SELECT * FROM acre_carrera ac WHERE ac.code_carrera = ?1 LIMIT 1", nativeQuery = true)
    public Carrera carreraPorCode(int code);

    @Query(value = "SELECT * FROM acre_carrera ac WHERE ac.nom_carrera = ?1 LIMIT 1", nativeQuery = true)
    public Carrera carreraPorNombre(String nombreCarrera);
}
