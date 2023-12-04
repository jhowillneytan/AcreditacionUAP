package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.TipoEvaluacionMateria;

public interface TipoEvaluacionMateriaDao extends CrudRepository<TipoEvaluacionMateria, Long>{
    @Query(value = "SELECT * FROM tipo_evaluacion_materia t WHERE t.nombre = ?1 LIMIT 1", nativeQuery = true)
    public TipoEvaluacionMateria evaluacionMateriaPorNombre(String nombre);
}
