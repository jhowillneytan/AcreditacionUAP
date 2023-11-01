package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Persona;

public interface IPersonaDao extends CrudRepository<Persona, Long>{
        @Query(value = "select * from acre_persona p where p.ci= ?1 LIMIT 1;", nativeQuery = true)
        public Persona personaCi(String ci);
}
