package com.uap.acreditacion.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.entity.Carrera;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.service.ICarreraService;
import com.uap.acreditacion.service.IFacultadService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
@RequestMapping("/carrera")
public class CarreraController {

    @Autowired
    private ICarreraService carreraService;

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
        model.addAttribute("carrera", new Carrera());
        model.addAttribute("carreras", carreraService.findAll());
        model.addAttribute("facultads", facultadService.findAll()); 

        if (success != null) {
            model.addAttribute("success", success);
        }

        return "Carrera/formulario";
		} else {
			return "redirect:/login";
		}
    }

    @GetMapping("/editar-carrera/{id}")
    public String editar(@PathVariable("id")Long id, Model model,@RequestParam(name = "success", required = false)String success, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
            Persona p = (Persona) request.getSession().getAttribute("persona");
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
        model.addAttribute("carrera", carreraService.findOne(id));
        model.addAttribute("carreras", carreraService.findAll());
        model.addAttribute("facultads", facultadService.findAll()); 

        if (success != null) {
            model.addAttribute("success", success);
        }

        return "Carrera/formulario";
		} else {
			return "redirect:/login";
		}
    }

    @GetMapping("/eliminar-carrera/{id}")
    public String eliminar(@PathVariable("id")Long id, RedirectAttributes flash){

        Carrera carrera = carreraService.findOne(id);
        carrera.setEstado("X");
        carreraService.save(carrera);

        flash.addAttribute("success", "Carrera Eliminado con Exito!");

        return "redirect:/carrera/formulario";
    }


    @PostMapping("/formulario")
    public String guardar(@Validated Carrera carrera, RedirectAttributes flash){
        
        carrera.setEstado("A");
        carreraService.save(carrera);

        flash.addAttribute("success", "Registro Exitoso!");

        return "redirect:/carrera/formulario";
    }

}
