package com.uap.acreditacion.dao;

import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Facultad;

public interface IFacultadDao extends CrudRepository<Facultad,Long>{
    
}
