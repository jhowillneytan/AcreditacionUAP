package com.uap.acreditacion.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Requisito;

public interface IRequisitoDao extends CrudRepository<Requisito,Long>{
    @Query(value = "select * from  materia m  "+
    "left join requisito_materia rm on m.id_materia = rm.id_materia "+
    "left join requisitos r ON rm.id_requisito = r.id_requisito "+
    "where m.id_materia = ?1 order by rm.id_requisito asc ;", nativeQuery = true )
    public List<Requisito> listaRequisitosMateria(Long id_materia);

    @Query(value = "select * from acre_carpeta ca "+
    "left join materia m on ca.id_carpeta = m.id_carpeta "+
    "left join requisito_materia rm on m.id_materia = rm.id_materia "+
    "left join requisitos r ON rm.id_requisito = r.id_requisito "+
    "left join parametro p on p.id_requisito = rm.id_requisito "+
    "left join acre_archivo aa on aa.id_parametro = p.id_parametro  "+
    "where m.id_materia = ?1 and m.id_carpeta = ?2 and ca.id_carpeta = aa.id_carpeta  "+
    "order by rm.id_requisito asc ;", nativeQuery = true )
    public List<Requisito> listaRequisitosMateria2(Long id_materia, Long id_carpeta);

}
