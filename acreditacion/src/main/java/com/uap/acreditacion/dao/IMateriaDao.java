package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Materia;

public interface IMateriaDao extends CrudRepository<Materia, Long> {
    @Query(value = "SELECT * FROM materia m WHERE m.nombre = ?1 and m.estado != 'X'  LIMIT 1", nativeQuery = true)
    public Materia materiaNombre(String nombre);

    @Query(value = "SELECT * FROM materia m WHERE m.sigla = ?1 and m.estado != 'X' LIMIT 1", nativeQuery = true)
    public Materia materiaSigla(String sigla);
}
