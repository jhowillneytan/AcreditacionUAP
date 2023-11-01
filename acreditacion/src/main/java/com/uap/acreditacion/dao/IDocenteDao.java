package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Docente;

public interface IDocenteDao extends CrudRepository<Docente, Long> {
    @Query(value = "SELECT * FROM acre_docente ad WHERE ad.rd = ?1 LIMIT 1", nativeQuery = true)
    public Docente docenteRD(String rd);

}
