package com.uap.acreditacion.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.entity.Cargo;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.service.ICargoService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
@RequestMapping(value="/cargo")
public class CargoController {

	@Autowired
	private ICargoService cargoService;

	@Autowired
	private	ITipoPersonaService tipoPersonaService;
	
	@Autowired
	private IPersonaService personaService;
	
	@GetMapping("/form-cargo")
	public String formCargo(ModelMap model, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
    	List<Cargo> listaCargos = cargoService.findAll();
		model.addAttribute("cargo", new Cargo());
		model.addAttribute("listaCargos", listaCargos);
		model.addAttribute("opcionCargo", "true");
		model.addAttribute("subMenuSeleccionado", "true");
		return "cargo";
	} else {
		return "redirect:/login";
	}
	}
	
	@PostMapping("/agregar-cargo")
	public String agregarCargo(RedirectAttributes redirectAttrs,
			@RequestParam(value = "nom_cargo") String nom_cargo,
			@RequestParam(value = "descripcion") String descripcion
			//@RequestParam(value = "estado") String estado,
			//@RequestParam(value = "fecha_registro") @DateTimeFormat(pattern = "yyyy-MM-dd")Date fecha_registro
	){
		Cargo cargo = new Cargo();
		cargo.setDescripcion(descripcion);
		cargo.setEstado("Activo");
		cargo.setFecha_registro(new Date());
		cargo.setNom_cargo(nom_cargo);
		cargoService.save(cargo);
		redirectAttrs
        .addFlashAttribute("mensaje", "Agregado correctamente")
        .addFlashAttribute("clase", "success");
		return "redirect:/cargo/form-cargo";
	}
	
	@GetMapping("/editar-cargo/{id_cargo}")
	public String getIdCargo(@PathVariable(value="id_cargo")Long id_cargo, ModelMap model, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
		Cargo cargo = cargoService.findOne(id_cargo);
		List<Cargo> listaCargos = cargoService.findAll();
		model.addAttribute("editMode", "true");
		model.addAttribute("listaCargos", listaCargos);
		model.addAttribute("cargo", cargo);
		model.addAttribute("opcionCargo", "true");
		model.addAttribute("subMenuSeleccionado", "true");
		return "cargo";
		} else {
			return "redirect:/login";
		}
	}
	
	@GetMapping("/cancelar-editar-cargo")
	public String cancelarCargo() {
		return "redirect:/cargo/form-cargo";
	}
	
	@PostMapping("/guardar-editado-cargo")
	public String guardarEditadoCargo(RedirectAttributes redirectAttrs,
			@RequestParam(value = "nom_cargo") String nom_cargo,
			@RequestParam(value = "descripcion") String descripcion,
			//@RequestParam(value = "estado") String estado,
			@RequestParam(value = "id_cargo") Long id_cargo
			//@RequestParam(value = "fecha_registro") @DateTimeFormat(pattern = "yyyy-MM-dd")Date fecha_registro
			) {
		Cargo cargo = cargoService.findOne(id_cargo);
		cargo.setDescripcion(descripcion);
		//cargo.setEstado(estado);
		//cargo.setFecha_registro(fecha_registro);
		cargo.setNom_cargo(nom_cargo);
		cargoService.save(cargo);
		redirectAttrs
        .addFlashAttribute("mensaje", "Actualizado correctamente")
        .addFlashAttribute("clase", "success");
		return "redirect:/cargo/form-cargo";
	}
	
	@GetMapping("/eliminar-cargo/{id_cargo}")
	public String eliminarCargo(@PathVariable(value="id_cargo")Long id_cargo, RedirectAttributes redirectAttrs) {
		cargoService.delete(id_cargo);
		redirectAttrs
        .addFlashAttribute("mensaje", "Eliminado correctamente")
        .addFlashAttribute("clase", "danger");
		System.out.println("eliminado cargo");
		return "redirect:/cargo/form-cargo";
	}
	
}
