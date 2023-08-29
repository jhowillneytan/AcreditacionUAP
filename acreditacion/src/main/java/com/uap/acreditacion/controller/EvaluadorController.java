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

import com.uap.acreditacion.entity.Carrera;
import com.uap.acreditacion.entity.Evaluador;
import com.uap.acreditacion.entity.Facultad;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.TipoPersona;
import com.uap.acreditacion.service.ICarreraService;
import com.uap.acreditacion.service.IEvaluadorService;
import com.uap.acreditacion.service.IFacultadService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
@RequestMapping(value="/evaluador")
public class EvaluadorController {

	@Autowired
	private IEvaluadorService evaluadorService;
	@Autowired
	private ITipoPersonaService tipoPersonaService;
	@Autowired
	private IPersonaService personaService;
	@Autowired
	private ICarreraService carreraService;
	@Autowired
	private IFacultadService facultadService;
	
	@GetMapping("/form-evaluador")
	public String formEvaluador(ModelMap model, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
    	
		List<Evaluador> listaEvaluadores = evaluadorService.findAll();
		List<TipoPersona> listaTipoPersonas = tipoPersonaService.findAll();
		List<Persona> listaPersonas = personaService.findAll();
		List<Facultad> listaFacultades = facultadService.findAll();
		List<Carrera> listaCarreras = carreraService.findAll();
		model.addAttribute("listaEvaluadores", listaEvaluadores);
		model.addAttribute("listaTipoPersonas", listaTipoPersonas);
		model.addAttribute("listaPersonas", listaPersonas);
		model.addAttribute("listaFacultades", listaFacultades);
		model.addAttribute("listaCarreras", listaCarreras);
		model.addAttribute("evaluador", new Evaluador());
		return "evaluador";
		} else {
			return "redirect:/login";
		}
	}
	
	@PostMapping("/agregar-evaluador")
	public String agregarEvaluador(RedirectAttributes redirectAttrs,
			@RequestParam(value = "gestion") Integer gestion,
			@RequestParam(value = "descripcion") String descripcion,
			//@RequestParam(value = "estado") String estado,
			@RequestParam(value = "persona") Persona persona,
			@RequestParam(value = "carrera") Carrera carrera
			//@RequestParam(value = "fecha_registro") @DateTimeFormat(pattern = "yyyy-MM-dd")Date fecha_registro
			) {
		System.out.println("entrando");
		Evaluador evaluador = new Evaluador();
		evaluador.setDescripcion(descripcion);
		evaluador.setEstado("Activo");
		evaluador.setFecha_registro(new Date());
		evaluador.setGestion(gestion);
		evaluador.setCarrera(carrera);
		evaluador.setPersona(persona);
		evaluadorService.save(evaluador);
		System.out.println("guardado evaluador");
		redirectAttrs
        .addFlashAttribute("mensaje", "Agregado correctamente")
        .addFlashAttribute("clase", "success");
		return "redirect:/evaluador/form-evaluador";
	}
	
	@GetMapping("/editar-evaluador/{id_evaluador}")
	public String formEvaluador(@PathVariable(value="id_evaluador")Long id_evaluador, ModelMap model, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
		List<Evaluador> listaEvaluadores = evaluadorService.findAll();
		List<TipoPersona> listaTipoPersonas = tipoPersonaService.findAll();
		List<Persona> listaPersonas = personaService.findAll();
		List<Facultad> listaFacultades = facultadService.findAll();
		List<Carrera> listaCarreras = carreraService.findAll();
		Persona p = (Persona) request.getSession().getAttribute("persona");
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
		Evaluador evaluador = evaluadorService.findOne(id_evaluador);
		model.addAttribute("evaluador", evaluador);
		model.addAttribute("editMode", "true");
		model.addAttribute("listaEvaluadores", listaEvaluadores);
		model.addAttribute("listaTipoPersonas", listaTipoPersonas);
		model.addAttribute("listaPersonas", listaPersonas);
		model.addAttribute("listaFacultades", listaFacultades);
		model.addAttribute("listaCarreras", listaCarreras);
		
		return "evaluador";
		} else {
			return "redirect:/login";
		}
	}
	
	@GetMapping("/cancelar-editar-evaluador")
	public String cancelarEditarTipoPersona() {
		return "redirect:/evaluador/form-evaluador";
	}
	
	@PostMapping("/guardar-editado-evaluador")
	public String guardarEditadoEvaluador(RedirectAttributes redirectAttrs,
			@RequestParam(value = "gestion") Integer gestion,
			@RequestParam(value = "id_evaluador") Long id_evaluador,
			@RequestParam(value = "descripcion") String descripcion,
			//@RequestParam(value = "estado") String estado,
			@RequestParam(value = "persona") Persona persona,
			@RequestParam(value = "carrera") Carrera carrera
			//@RequestParam(value = "fecha_registro") @DateTimeFormat(pattern = "yyyy-MM-dd")Date fecha_registro
			) {
		System.out.println("entrando");
		Evaluador evaluador = evaluadorService.findOne(id_evaluador);
		evaluador.setDescripcion(descripcion);
		//evaluador.setEstado(estado);
		//evaluador.setFecha_registro(fecha_registro);
		evaluador.setGestion(gestion);
		evaluador.setCarrera(carrera);
		evaluador.setPersona(persona);
		evaluadorService.save(evaluador);
		System.out.println("actualizado evaluador");
		redirectAttrs
        .addFlashAttribute("mensaje", "Actualizado correctamente")
        .addFlashAttribute("clase", "success");
		return "redirect:/evaluador/form-evaluador";
	}
	
	@GetMapping("/eliminar-evaluador/{id_evaluador}")
	public String eliminarEvaluador(@PathVariable(value="id_evaluador")Long id_evaluador, RedirectAttributes redirectAttrs) {
		evaluadorService.delete(id_evaluador);
		System.out.println("eliminado evaluador");
		redirectAttrs
        .addFlashAttribute("mensaje", "Eliminado satisfactoriamente")
        .addFlashAttribute("clase", "danger");
		
		return "redirect:/evaluador/form-evaluador";
	}
	
	
}
