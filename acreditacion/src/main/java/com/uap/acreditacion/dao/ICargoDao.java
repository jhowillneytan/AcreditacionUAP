package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Cargo;

public interface ICargoDao extends CrudRepository<Cargo,Long>{
    @Query(value = "SELECT * FROM acre_cargo a WHERE a.nom_cargo = ?1 LIMIT 1", nativeQuery = true)
    public Cargo cargoNombre(String nombre);
}
