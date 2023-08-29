package com.uap.acreditacion.controller;


import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.TipoArchivo;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoArchivoService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
@RequestMapping("/tipo-archivo")
public class TipoArchivoController {
	
	Config config = new Config();

    @Autowired
    private ITipoArchivoService tipoArchivoService;
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
        model.addAttribute("tipoArchivo", new TipoArchivo());
        model.addAttribute("tipoArchivos", tipoArchivoService.findAll());

        if (success != null) {
            model.addAttribute("success", success);
        }

        return "TipoArchivo/formulario";
		} else {
			return "redirect:/login";
		}
    }

    @GetMapping("/editar-tipo-archivo/{id}")
    public String editar(@PathVariable("id")Long id, Model model,@RequestParam(name = "success", required = false)String success, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
    	
            Persona p = (Persona) request.getSession().getAttribute("persona");
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
        model.addAttribute("tipoArchivo", tipoArchivoService.findOne(id));
        model.addAttribute("tipoArchivos", tipoArchivoService.findAll());

        if (success != null) {
            model.addAttribute("success", success);
        }

        return "TipoArchivo/formulario";
		} else {
			return "redirect:/login";
		}
    }

    @GetMapping("/eliminar-tipo-archivo/{id}")
    public String eliminar(@PathVariable("id")Long id, RedirectAttributes flash){

        TipoArchivo tipoArchivo = tipoArchivoService.findOne(id);
        tipoArchivo.setEstado("X");
        tipoArchivoService.save(tipoArchivo);

        flash.addAttribute("success", "Tipo Archivo Eliminado con Exito!");

        return "redirect:/tipo-archivo/formulario";
    }


    @PostMapping("/formulario")
    public String guardar(@Validated TipoArchivo tipoArchivo, RedirectAttributes flash, @RequestParam(name = "archivo", required = false) MultipartFile file){
        tipoArchivo.setEstado("A");
        if (!file.isEmpty()) {
            //byte[] contenido;
            
               // contenido = file.getBytes();
                //tipoArchivo.setIcono(file);
           
                // TODO Auto-generated catch block
            
        	
		}
        
        tipoArchivoService.save(tipoArchivo);

        flash.addAttribute("success", "Registro Exitoso!");

        return "redirect:/tipo-archivo/formulario";
    }

    /*@GetMapping("/verImagen/{id}")
public ResponseEntity<ByteArrayResource> verImagen(@PathVariable Long id) {
    Optional<TipoArchivo> tipoArchivoOptional = tipoArchivoService.findOneOptional(id);

    if (tipoArchivoOptional.isPresent()) {
        TipoArchivo tipoArchivo = tipoArchivoOptional.get();
        byte[] contenido = tipoArchivo.getIcono();
        ByteArrayResource resource = new ByteArrayResource(contenido);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=imagen.jpg") // Cambiar la extensión del archivo según el tipo de imagen guardada
                .contentType(MediaType.IMAGE_JPEG) // Cambiar a MediaType.IMAGE_JPEG para imágenes JPEG o MediaType.IMAGE_PNG para imágenes PNG
                .contentLength(contenido.length)
                .body(resource);
    } else {
        return ResponseEntity.notFound().build();
    }
}
*/

}
