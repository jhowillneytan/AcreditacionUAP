package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.IEvaluadorDao;
import com.uap.acreditacion.entity.Evaluador;

@Service
public class EvaluadorServiceImpl implements IEvaluadorService{

    @Autowired
    private IEvaluadorDao evaluadorDao;

    @Override
    public List<Evaluador> findAll() {
        // TODO Auto-generated method stub
        return (List<Evaluador>) evaluadorDao.findAll();
    }

    @Override
    public void save(Evaluador endidad) {
        // TODO Auto-generated method stub
        evaluadorDao.save(endidad);
    }

    @Override
    public Evaluador findOne(Long id) {
        // TODO Auto-generated method stub
        return evaluadorDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        evaluadorDao.deleteById(id);
    }

	@Override
	public Evaluador getEvaluadorPersonaGestion(Long id_persona, Integer gestion) {
		// TODO Auto-generated method stub
		return evaluadorDao.getEvaluadorPersonaGestion(id_persona, gestion);
	}
    
}
