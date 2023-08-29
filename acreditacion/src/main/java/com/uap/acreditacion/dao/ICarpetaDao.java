package com.uap.acreditacion.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uap.acreditacion.entity.Carpeta;

public interface ICarpetaDao extends JpaRepository<Carpeta,Long>{
	
	@Query("select c from Carpeta c left join c.carrera ca where ca.id_carrera=?1 order by c.id_carpeta asc")
	public List<Carpeta> getAllCarpetasCarrera(Long id_carrera);

	//@Query("select c from Carpeta c where c.usuarios.id_usuario=?1 order by c.id_carpeta asc")
	//public List<Carpeta> getAllCarpetasUsuario(Long id_usuario);
}
