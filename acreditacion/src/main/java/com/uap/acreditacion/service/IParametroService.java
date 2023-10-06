package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Parametro;

public interface IParametroService {
    public List<Parametro> findAll();

    public void save(Parametro parametro);

    public Parametro findOne(Long id);

    public void delete(Long id);
}
