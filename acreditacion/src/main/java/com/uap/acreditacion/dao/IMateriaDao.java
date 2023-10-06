package com.uap.acreditacion.dao;

import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Materia;

public interface IMateriaDao extends CrudRepository<Materia,Long>{
    
}
