package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.IPuestoDao;
import com.uap.acreditacion.entity.Puesto;

@Service
public class PuestoServiceImpl implements IPuestoService{

    @Autowired
    private IPuestoDao puestoDao;

    @Override
    public List<Puesto> findAll() {
        // TODO Auto-generated method stub
        return (List<Puesto>) puestoDao.findAll();
    }

    @Override
    public void save(Puesto endidad) {
        // TODO Auto-generated method stub
        puestoDao.save(endidad);
    }

    @Override
    public Puesto findOne(Long id) {
        // TODO Auto-generated method stub
        return puestoDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        puestoDao.deleteById(id);
    }

    @Override
    public Puesto puestoNombre(String nombre) {
        return puestoDao.puestoNombre(nombre);
    }
    
}
