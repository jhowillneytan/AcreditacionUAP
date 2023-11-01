package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.ITipoPersonaDao;
import com.uap.acreditacion.entity.TipoPersona;

@Service
public class TipoPersonaServiceImpl implements ITipoPersonaService{
    
    @Autowired
    private ITipoPersonaDao tipoPersonaDao;

    @Override
    public List<TipoPersona> findAll() {
        // TODO Auto-generated method stub
        return (List<TipoPersona>) tipoPersonaDao.findAll();
    }

    @Override
    public void save(TipoPersona endidad) {
        // TODO Auto-generated method stub
        tipoPersonaDao.save(endidad);
    }

    @Override
    public TipoPersona findOne(Long id) {
        return tipoPersonaDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        tipoPersonaDao.deleteById(id);
    }

    @Override
    public TipoPersona tipoPersonaNombre(String nombre) {
        return tipoPersonaDao.tipoPersonaNombre(nombre);
    }
    
}
