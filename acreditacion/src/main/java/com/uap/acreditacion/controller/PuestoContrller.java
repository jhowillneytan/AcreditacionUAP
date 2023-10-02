package com.uap.acreditacion.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.Puesto;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.IPuestoService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
@RequestMapping(value ="/puesto")
public class PuestoContrller {

	@Autowired
	private IPuestoService puestoService;
	@Autowired
	private ITipoPersonaService tipoPersonaService;

	@Autowired
	private IPersonaService personaService;

	@GetMapping("/form-puesto")
	public String formPuesto(ModelMap model, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
		List<Puesto> listaPuestos = puestoService.findAll();
		model.addAttribute("listaPuestos", listaPuestos);
		model.addAttribute("puesto", new Puesto());
		return "puesto";
		} else {
			return "redirect:/login"; 
		}
	}
	
	@PostMapping("/agregar-puesto")
	public String agregarPuesto(RedirectAttributes redirectAttrs,
			@RequestParam(value = "nom_puesto") String nom_puesto,
			@RequestParam(value = "descripcion") String descripcion
			//@RequestParam(value = "estado") String estado,
			//@RequestParam(value = "fecha_registro") @DateTimeFormat(pattern = "yyyy-MM-dd")Date fecha_registro
			) {
		Puesto puesto = new Puesto();
		puesto.setDescripcion(descripcion);
		puesto.setEstado("Activo");
		puesto.setFecha_registro(new Date());
		puesto.setNom_puesto(nom_puesto);
		puestoService.save(puesto);
		redirectAttrs
        .addFlashAttribute("mensaje", "Agregado correctamente")
        .addFlashAttribute("clase", "success");
		System.out.println("puesto guardado");
		return "redirect:/puesto/form-puesto";
	}
	
	@GetMapping("/editar-puesto/{id_puesto}")
	public String getIdPuesto(@PathVariable(value="id_puesto")Long id_puesto, ModelMap model, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
    	
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
		Puesto puesto = puestoService.findOne(id_puesto);
		model.addAttribute("puesto", puesto);
		List<Puesto> listaPuestos = puestoService.findAll();
		model.addAttribute("listaPuestos", listaPuestos);
		model.addAttribute("editMode", "true");
		System.out.println("id="+id_puesto);
		return "puesto";
		} else {
			return "redirect:/login";
		}
	}
	
	@GetMapping("/cancelar-editar-puesto")
	public String cancelarPuesto() {
		return "redirect:/puesto/form-puesto";
	}
	
	@PostMapping("/guardar-editado-puesto")
	public String guardarEditadoPuesto(RedirectAttributes redirectAttrs,
			@RequestParam(value = "nom_puesto") String nom_puesto,
			@RequestParam(value = "descripcion") String descripcion,
			//@RequestParam(value = "estado") String estado,
			@RequestParam(value = "id_puesto") Long id_puesto
			//@RequestParam(value = "fecha_registro") @DateTimeFormat(pattern = "yyyy-MM-dd")Date fecha_registro
			) {
		Puesto puesto = puestoService.findOne(id_puesto);
		puesto.setDescripcion(descripcion);
		//puesto.setEstado(estado);
		//puesto.setFecha_registro(fecha_registro);
		puesto.setNom_puesto(nom_puesto);
		puestoService.save(puesto);
		redirectAttrs
        .addFlashAttribute("mensaje", "Actualizado correctamente")
        .addFlashAttribute("clase", "success");
		System.out.println("puesto actualizado");
		return "redirect:/puesto/form-puesto";
	}
	
	@GetMapping("/eliminar-puesto/{id_puesto}")
	public String eliminarPuesto(@PathVariable(value="id_puesto")Long id_puesto, RedirectAttributes redirectAttrs) {
		puestoService.delete(id_puesto);
		redirectAttrs
        .addFlashAttribute("mensaje", "Eliminado correctamente")
        .addFlashAttribute("clase", "danger");
		System.out.println("puesto eliminado");
		return "redirect:/puesto/form-puesto";
	}
	
}
