package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Puesto;

public interface IPuestoDao extends CrudRepository<Puesto, Long>{
    @Query(value = "SELECT * FROM acre_puesto a WHERE a.nom_puesto = ?1 LIMIT 1", nativeQuery = true)
    public Puesto puestoNombre(String nombre);
}
