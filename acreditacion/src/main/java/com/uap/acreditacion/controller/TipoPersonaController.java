package com.uap.acreditacion.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.TipoPersona;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
@RequestMapping(value = "/tipo-persona")
public class TipoPersonaController {

	@Autowired
	private ITipoPersonaService tipoPersonaService;

	@Autowired
	private IPersonaService personaService;
	
	@GetMapping(value = "/form-tipo-persona")
	public String formTipoPersona(Model model, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
			//Persona p = (Persona) request.getSession().getAttribute("persona");
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
		List<TipoPersona> listaTipoPersonas = tipoPersonaService.findAll();
		model.addAttribute("tipoPersona", new TipoPersona());
		model.addAttribute("listaTipoPersonas", listaTipoPersonas);
		return "tipo_persona";

		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/agregar-tipo-persona")
	ModelAndView agregarTipoPersona(@Validated TipoPersona tipoPersona, ModelMap model, RedirectAttributes redirectAttrs, BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
			List<TipoPersona> listaTipoPersonas = tipoPersonaService.findAll();
			return new ModelAndView("tipo_persona")
					.addObject("tipoPersona", tipoPersona)
					.addObject("listaTipoPersonas", listaTipoPersonas);
		}
		tipoPersona.setFecha_registro(new Date());
		tipoPersona.setEstado("Activo");
		tipoPersonaService.save(tipoPersona);
		redirectAttrs
        .addFlashAttribute("mensaje", "Agregado correctamente")
        .addFlashAttribute("clase", "success");
		return new ModelAndView("redirect:/tipo-persona/form-tipo-persona");
	}
	
	@GetMapping("/editar-tipo-persona/{id_tipo_persona}")
	public String getIdTipoPersona(@PathVariable (value="id_tipo_persona") Long id_tipo_persona, ModelMap model, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
    	
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
		TipoPersona tipoPersona = tipoPersonaService.findOne(id_tipo_persona);
		model.addAttribute("tipoPersona", tipoPersona);
		model.addAttribute("editMode", "true");
		List<TipoPersona> listaTipoPersonas = tipoPersonaService.findAll();
		model.addAttribute("listaTipoPersonas", listaTipoPersonas);
		return "tipo_persona";
		} else {
			return "redirect:/login";
		}
	}
	
	@GetMapping("/cancelar-editar-tipo-persona")
	public String cancelarEditarTipoPersona() {
		return "redirect:/tipo-persona/form-tipo-persona";
	}
	
	@PostMapping("/guardar-editado-tipo-persona")
	public String guardarEditadoTipoPersona(RedirectAttributes redirectAttrs,
			@RequestParam(value = "id_tipo_persona") Long id_tipo_persona,
			@RequestParam(value = "nom_tipo_persona") String nom_tipo_persona,
			@RequestParam(value = "descripcion") String descripcion,
			@RequestParam(value = "estado") String estado
			/*@RequestParam(value = "fecha_registro") @DateTimeFormat(pattern = "yyyy-MM-dd")Date fecha_registro*/) {
		System.out.println("id======"+id_tipo_persona);
		TipoPersona tipoPersona = tipoPersonaService.findOne(id_tipo_persona);
		tipoPersona.setNom_tipo_persona(nom_tipo_persona);
		tipoPersona.setDescripcion(descripcion);
		tipoPersona.setEstado(estado);
		tipoPersona.setFecha_registro(new Date());
		tipoPersonaService.save(tipoPersona);
		redirectAttrs
        .addFlashAttribute("mensaje", "Actualizado correctamente")
        .addFlashAttribute("clase", "success");
		return "redirect:/tipo-persona/form-tipo-persona";
	}
	
	@GetMapping("/eliminar-tipo-persona/{id_tipo_persona}")
	public String eliminarTipoPersona(@PathVariable (value="id_tipo_persona") Long id_tipo_persona, RedirectAttributes redirectAttrs) {
		tipoPersonaService.delete(id_tipo_persona);
		System.out.println("eliminado tipoPersona");
		redirectAttrs
        .addFlashAttribute("mensaje", "Eliminado correctamente")
        .addFlashAttribute("clase", "danger");
		return "redirect:/tipo-persona/form-tipo-persona";
	}

	
	
	
	
}
