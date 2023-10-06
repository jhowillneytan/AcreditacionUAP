package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Requisito;

public interface IRequisitoService {
    public List<Requisito> findAll();

    public void save(Requisito requisito);

    public Requisito findOne(Long id);

    public void delete(Long id);
}
