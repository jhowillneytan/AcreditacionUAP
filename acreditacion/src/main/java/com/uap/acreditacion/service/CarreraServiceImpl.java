package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.ICarreraDao;
import com.uap.acreditacion.entity.Carrera;

@Service
public class CarreraServiceImpl implements ICarreraService{

    @Autowired
    private ICarreraDao carreraDao;

    @Override
    public List<Carrera> findAll() {
        // TODO Auto-generated method stub
        return (List<Carrera>) carreraDao.findAll();
    }

    @Override
    public void save(Carrera endidad) {
        // TODO Auto-generated method stub
        carreraDao.save(endidad);
    }

    @Override
    public Carrera findOne(Long id) {
        // TODO Auto-generated method stub
        return carreraDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        carreraDao.deleteById(id);
    }
    
}
