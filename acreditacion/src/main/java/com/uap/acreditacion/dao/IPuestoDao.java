package com.uap.acreditacion.dao;

import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Puesto;

public interface IPuestoDao extends CrudRepository<Puesto, Long>{
    
}
