package com.uap.acreditacion.dao;

import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Persona;

public interface IPersonaDao extends CrudRepository<Persona, Long>{
 
}
