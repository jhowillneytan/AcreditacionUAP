package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Parametro;

public interface IParametroService {
    public List<Parametro> findAll();

    public void save(Parametro parametro);

    public Parametro findOne(Long id);

    public void delete(Long id);

    public List<Parametro> listaParametro2(Long id_carpeta, Long id_materia, Long id_requisito);
}
