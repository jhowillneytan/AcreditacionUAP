package com.uap.acreditacion.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import com.uap.acreditacion.entity.Facultad;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.service.IFacultadService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
@RequestMapping("/facultad")
public class FacultadController {
    
    @Autowired
    private IFacultadService facultadService;
    @Autowired
	private ITipoPersonaService tipoPersonaService;
    
    @Autowired
	private IPersonaService personaService;
    
    @GetMapping("/formulario")
    public String form(Model model,@RequestParam(name = "success", required = false)String success, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
    	
            Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
        model.addAttribute("facultad", new Facultad());
        model.addAttribute("facultads", facultadService.findAll());

        if (success != null) {
            model.addAttribute("success", success);
        }
        model.addAttribute("opcionFacultad", "true");
        return "Facultad/formulario";
		} else {
			return "redirect:/login";
		}
    }

    @GetMapping("/editar-facultad/{id}")
    public String editar(@PathVariable("id")Long id, Model model,@RequestParam(name = "success", required = false)String success, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
            Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
        model.addAttribute("facultad", facultadService.findOne(id));
        model.addAttribute("facultads", facultadService.findAll());

        if (success != null) {
            model.addAttribute("success", success);
        }
        model.addAttribute("opcionFacultad", "true");
        return "Facultad/formulario";
		} else {
			return "redirect:/login";
		}
    }

    @GetMapping("/eliminar-facultad/{id}")
    public String eliminar(@PathVariable("id")Long id, RedirectAttributes flash){

        Facultad facultad = facultadService.findOne(id);
        facultad.setEstado("X");
        facultadService.save(facultad);

        flash.addAttribute("success", "Facultad Eliminado con Exito!");

        return "redirect:/facultad/formulario";
    }


    @PostMapping("/formulario")
    public String guardar(@Validated Facultad facultad, RedirectAttributes flash){
        
        facultad.setEstado("A");
        facultadService.save(facultad);

        flash.addAttribute("success", "Registro Exitoso!");

        return "redirect:/facultad/formulario";
    }

}
