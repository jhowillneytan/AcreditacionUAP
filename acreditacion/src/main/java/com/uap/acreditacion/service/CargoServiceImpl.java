package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.ICargoDao;
import com.uap.acreditacion.entity.Cargo;

@Service
public class CargoServiceImpl implements ICargoService{

    @Autowired
    private ICargoDao cargoDao;

    @Override
    public List<Cargo> findAll() {
        // TODO Auto-generated method stub
        return (List<Cargo>) cargoDao.findAll();
    }
 
    @Override
    public void save(Cargo endidad) {
        // TODO Auto-generated method stub
        cargoDao.save(endidad);
    }

    @Override
    public Cargo findOne(Long id) {
        // TODO Auto-generated method stub
        return cargoDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        cargoDao.deleteById(id);
    }
    
}
