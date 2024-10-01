package com.uap.acreditacion.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Parametro;
import com.uap.acreditacion.entity.Requisito;

public interface IParametroDao extends CrudRepository<Parametro, Long> {
    // @Query(value = "select * from parametro p " +
    // "left join requisitos r on r.id_requisito = p.id_requisito " +
    // "left join requisito_materia rm on rm.id_requisito = r.id_requisito " +
    // "left join materia m on m.id_materia = rm.id_materia " +
    // "left join acre_carpeta ac on ac.id_carpeta = m.id_carpeta " +
    // "where m.id_carpeta = ac.id_carpeta and rm.id_materia = m.id_materia " +
    // "and rm.id_requisito = r.id_requisito " +
    // "and ac.id_carpeta = ?1 and m.id_materia = ?2 and rm.id_requisito = ?3 " +
    // "order by p.id_parametro asc ;", nativeQuery = true)
    // public List<Parametro> listaParametro2(Long id_carpeta, Long id_materia, Long
    // id_requisito);

    @Query(value = "SELECT p.* FROM parametro p " +
            "LEFT JOIN requisitos r ON r.id_requisito = p.id_requisito " +
            "LEFT JOIN requisito_materia rm ON rm.id_requisito = r.id_requisito " +
            "LEFT JOIN materia m ON m.id_materia = rm.id_materia " +
            "LEFT JOIN acre_carpeta ac ON ac.id_carpeta = m.id_carpeta " +
            "WHERE ac.id_carpeta = ?1 " +
            "AND m.id_materia = ?2 " +
            "AND rm.id_requisito = ?3 " +
            "ORDER BY p.id_parametro ASC", nativeQuery = true)
    public List<Parametro> listaParametro2(Long id_carpeta, Long id_materia, Long id_requisito);

}
