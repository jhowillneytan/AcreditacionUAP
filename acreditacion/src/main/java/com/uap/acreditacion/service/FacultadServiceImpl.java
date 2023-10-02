package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.IFacultadDao;
import com.uap.acreditacion.entity.Facultad;

@Service
public class FacultadServiceImpl implements IFacultadService{

    @Autowired
    private IFacultadDao facultadDao;

    @Override
    public List<Facultad> findAll() {
        // TODO Auto-generated method stub
        return (List<Facultad>) facultadDao.findAll();
    }
  
    @Override
    public void save(Facultad endidad) {
        // TODO Auto-generated method stub
        facultadDao.save(endidad);
    }

    @Override
    public Facultad findOne(Long id) {
        // TODO Auto-generated method stub
        return facultadDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        facultadDao.deleteById(id);
    }
    
}
