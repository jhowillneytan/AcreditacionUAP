package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.TipoPersona;

public interface ITipoPersonaDao extends CrudRepository<TipoPersona, Long>{
        @Query(value = "SELECT * FROM acre_tipo_persona a WHERE a.nom_tipo_persona = ?1 LIMIT 1", nativeQuery = true)
    public TipoPersona tipoPersonaNombre(String nombre);
}
