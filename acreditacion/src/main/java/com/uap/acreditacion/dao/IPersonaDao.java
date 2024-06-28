package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Persona;

public interface IPersonaDao extends CrudRepository<Persona, Long>{
        @Query(value = "select * from acre_persona p where p.ci= ?1 and p.estado !='X';", nativeQuery = true)
        public Persona personaCi(String ci);

        @Query(value = "select * from acre_persona p where p.ci != ?1 and p.ci=?2 and p.estado !='X'", nativeQuery = true)
        public Persona personaModCi(String ciActual, String ciNuevo);
}
