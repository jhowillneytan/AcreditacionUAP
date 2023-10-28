package com.uap.acreditacion.dao;

import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Docente;

public interface IDocenteDao extends CrudRepository<Docente,Long> {
    
}
