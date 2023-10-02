package com.uap.acreditacion.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.Config;
import com.uap.acreditacion.entity.Carrera;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.service.ICarreraService;
import com.uap.acreditacion.service.IFacultadService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
@RequestMapping("/carrera")
public class CarreraController {

    Config config = new Config();

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
             Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
        model.addAttribute("carrera", carreraService.findOne(id));
        model.addAttribute("carreras", carreraService.findAll());
        model.addAttribute("facultads", facultadService.findAll()); 
        model.addAttribute("edit", "true"); 
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

    @PostMapping("/RegistrarCarrera")
    public String RegistrarCarrera(@Validated Carrera carrera, RedirectAttributes flash,
    @RequestParam(value = "Logo")MultipartFile file){
		System.out.println("AAAAAAAAAAAAAAAAAAA METOOD");
    
        carrera.setEstado("A");
        if (!file.isEmpty()) {
            String arch = config.guardarArchivo(file);
            carrera.setFile(arch);
            String[] ta = arch.split("\\.");
            for (String string : ta) {
                System.out.println(string);
            }
        } else {
            flash
                    .addFlashAttribute("mensaje", "Es necesario cargar un imagen")
                    .addFlashAttribute("clase", "danger");
            return "redirect:/carrera/formulario/";
        }

        carreraService.save(carrera);
        flash.addAttribute("success", "Registro Exitoso!");

        return "redirect:/carrera/formulario";
    }

    /*@PostMapping("/RegistrarCarrera")
    public String guardar(@Validated Carrera carrera, RedirectAttributes flash,
    @RequestParam(value = "Logo")MultipartFile file){
    System.out.println("AAAAAAAAAAAAAAAAAAA METOOD");
    
        carrera.setEstado("A");
        if (!file.isEmpty()) {
            String arch = config.guardarArchivo(file);
            carrera.setFile(arch);
            String[] ta = arch.split("\\.");
            for (String string : ta) {
                System.out.println(string);
            }
        } else {
            flash
                    .addFlashAttribute("mensaje", "Es necesario cargar un imagen")
                    .addFlashAttribute("clase", "danger");
            return "redirect:/formulario/";
        }

        carreraService.save(carrera);
        flash.addAttribute("success", "Registro Exitoso!");

        return "redirect:/formulario";
    }*/
    @PostMapping("/GuardarCambiosCarrera")
    public String GuardarCambiosCarrera(@Validated Carrera carrera, RedirectAttributes flash,
    @RequestParam(value = "Logo")MultipartFile file){
    Carrera carrera2 = carreraService.findOne(carrera.getId_carrera());
        carrera.setEstado("A");
        if (file.isEmpty()) {
            carrera.setFile(carrera2.getFile());
        }
        carreraService.save(carrera);
        flash.addAttribute("success", "Se ha guardado los cambios!");

        return "redirect:/carrera/formulario";
    }

}
