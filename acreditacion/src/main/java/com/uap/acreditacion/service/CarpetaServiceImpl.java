package com.uap.acreditacion.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.ICarpetaDao;
import com.uap.acreditacion.entity.Carpeta;

@Service
public class CarpetaServiceImpl implements ICarpetaService{

    @Autowired
    private ICarpetaDao carpetaDao;

    @Override
    public List<Carpeta> findAll() {
        // TODO Auto-generated method stub
        return (List<Carpeta>) carpetaDao.findAll();
    }
 
    @Override
    public void save(Carpeta endidad) {
        // TODO Auto-generated method stub
        carpetaDao.save(endidad);
    }

    @Override
    public Carpeta findOne(Long id) {
        // TODO Auto-generated method stub
        return carpetaDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        carpetaDao.deleteById(id);
    }

	@Override
	public List<Carpeta> getAllCarpetasCarrera(Long id_carrera) {
		return carpetaDao.getAllCarpetasCarrera(id_carrera);
	}

    @Override
    public List<Carpeta> getAllCarpetasUsuario(Long id_usuario) {
        return carpetaDao.getAllCarpetasUsuario(id_usuario);
    }

    @Override
    public List<Carpeta> listaDireccionesParaMoverArchivo(Long idCarpeta, Long idUsuario) {
        return carpetaDao.listaDireccionesParaMoverArchivo(idCarpeta, idUsuario);
    }

    @Override
    public List<Carpeta> listaDireccionesParaMoverArchivoAdmin(Long idCarpeta) {
        return carpetaDao.listaDireccionesParaMoverArchivoAdmin(idCarpeta);
    }

    @Override
    public List<Carpeta> listaDireccionesParaMoverArchivoDocente(Long idCarpeta, Long idDocente) {
        return carpetaDao.listaDireccionesParaMoverArchivoDocente(idCarpeta, idDocente);
    }

    @Override
    public List<Carpeta> getCarpetasUsuarioYHijos(Long id_usuario, Long id_carpeta) {
        return carpetaDao.getCarpetasUsuarioYHijos(id_usuario, id_carpeta);
    }

    @Override
    public List<Carpeta> getAllCarpetasNullPadreUsuario(Long id_usuario) {
        return carpetaDao.getAllCarpetasNullPadreUsuario(id_usuario);
    }

}
