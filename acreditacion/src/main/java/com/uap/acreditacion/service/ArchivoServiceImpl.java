package com.uap.acreditacion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.IArchivoDao;
import com.uap.acreditacion.entity.Archivo;

@Service
public class ArchivoServiceImpl implements IArchivoService{

    @Autowired
    private IArchivoDao archivoDao;

    @Override
    public List<Archivo> findAll() {
        // TODO Auto-generated method stub
        return (List<Archivo>) archivoDao.findAll();
    }

    @Override
    public void save(Archivo endidad) {
        // TODO Auto-generated method stub 
        archivoDao.save(endidad);
    }

    @Override
    public Archivo findOne(Long id) {
        // TODO Auto-generated method stub
        return archivoDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        archivoDao.deleteById(id);
    }

	@Override
	public List<Archivo> listaArchivosPorDescripcion(String descripcion, Long id_usuario) {
		// TODO Auto-generated method stub
		return archivoDao.listaArchivosPorDescripcion(descripcion, id_usuario);
	}

    @Override
    public Optional<Archivo> findOneOptional(Long id) {
        return archivoDao.findById(id);
    }

    @Override
    public List<Archivo> listaArchivosPorDescripcionAdmin(String descripcion) {
        return archivoDao.listaArchivosPorDescripcionAdmin(descripcion);
    }
    
}
