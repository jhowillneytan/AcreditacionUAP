package com.uap.acreditacion.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.Config;
import com.uap.acreditacion.entity.Carrera;
import com.uap.acreditacion.entity.Facultad;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.service.ICarreraService;
import com.uap.acreditacion.service.IFacultadService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;

import jakarta.servlet.http.HttpServletRequest;

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
    public String form(Model model, @RequestParam(name = "success", required = false) String success,
            HttpServletRequest request) {
        if (request.getSession().getAttribute("persona") != null) {
            Persona p2 = (Persona) request.getSession().getAttribute("persona");
            Persona p = personaService.findOne(p2.getId_persona());
            model.addAttribute("personasession", p);
            model.addAttribute("tipoPersonasession",
                    tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
            model.addAttribute("carrera", new Carrera());
            model.addAttribute("carreras", carreraService.findAll());
            model.addAttribute("facultads", facultadService.findAll());

            if (success != null) {
                model.addAttribute("success", success);
            }
            model.addAttribute("opcionCarrera", "true");
            model.addAttribute("subMenuSeleccionado", "true");
            return "Carrera/formulario";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/editar-carrera/{id}")
    public String editar(@PathVariable("id") Long id, Model model,
            @RequestParam(name = "success", required = false) String success, HttpServletRequest request) {
        if (request.getSession().getAttribute("persona") != null) {
            Persona p2 = (Persona) request.getSession().getAttribute("persona");
            Persona p = personaService.findOne(p2.getId_persona());
            model.addAttribute("personasession", p);
            model.addAttribute("tipoPersonasession",
                    tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
            model.addAttribute("carrera", carreraService.findOne(id));
            model.addAttribute("carreras", carreraService.findAll());
            model.addAttribute("facultads", facultadService.findAll());
            model.addAttribute("edit", "true");
            if (success != null) {
                model.addAttribute("success", success);
            }
            model.addAttribute("opcionCarrera", "true");
            model.addAttribute("subMenuSeleccionado", "true");
            return "Carrera/formulario";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/eliminar-carrera/{id}")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes flash) {

        Carrera carrera = carreraService.findOne(id);
        carrera.setEstado("X");
        carreraService.save(carrera);

        flash.addAttribute("success", "Carrera Eliminado con Exito!");

        return "redirect:/carrera/formulario";
    }

    @PostMapping("/RegistrarCarrera")
    public String RegistrarCarrera(@Validated Carrera carrera, RedirectAttributes flash,
            @RequestParam(value = "Logo") MultipartFile file) {
        //System.out.println("AAAAAAAAAAAAAAAAAAA METOOD");

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

    /*
     * @PostMapping("/RegistrarCarrera")
     * public String guardar(@Validated Carrera carrera, RedirectAttributes flash,
     * 
     * @RequestParam(value = "Logo")MultipartFile file){
     * System.out.println("AAAAAAAAAAAAAAAAAAA METOOD");
     * 
     * carrera.setEstado("A");
     * if (!file.isEmpty()) {
     * String arch = config.guardarArchivo(file);
     * carrera.setFile(arch);
     * String[] ta = arch.split("\\.");
     * for (String string : ta) {
     * System.out.println(string);
     * }
     * } else {
     * flash
     * .addFlashAttribute("mensaje", "Es necesario cargar un imagen")
     * .addFlashAttribute("clase", "danger");
     * return "redirect:/formulario/";
     * }
     * 
     * carreraService.save(carrera);
     * flash.addAttribute("success", "Registro Exitoso!");
     * 
     * return "redirect:/formulario";
     * }
     */
    @PostMapping("/GuardarCambiosCarrera")
    public String GuardarCambiosCarrera(@Validated Carrera carrera, RedirectAttributes flash,
            @RequestParam(value = "Logo") MultipartFile file) {
        Carrera carrera2 = carreraService.findOne(carrera.getId_carrera());
        carrera.setEstado("A");
        carrera.setFecha_registro(carrera2.getFecha_registro());
        if (file.isEmpty()) {
            carrera.setFile(carrera2.getFile());
        } else {
            String arch = config.guardarArchivo(file);
            carrera.setFile(arch);
            String[] ta = arch.split("\\.");
            for (String string : ta) {
                System.out.println(string);
            }
        }
        carreraService.save(carrera);
        flash.addAttribute("success", "Se ha guardado los cambios!");

        return "redirect:/carrera/formulario";
    }

    @GetMapping("/cargarCarreras")
    public String cargarCarreras(HttpServletRequest request) {
        Map<String, Object> requests = new HashMap<String, Object>();

        String url = "http://181.115.188.250:9993/v1/service/api/89b5b361047f40edb5d75dce872e8bf1";
        String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", key);

        HttpEntity<HashMap> req = new HttpEntity(requests, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, req, Map.class);
        List<Facultad> facultads = facultadService.findAll();
        if (resp.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseMap = resp.getBody();

            if (responseMap != null) {
                List<Map<String, String>> dataList = (List<Map<String, String>>) responseMap.get("data");

                if (dataList != null && !dataList.isEmpty()) {

                    for (Map<String, String> carreraData : dataList) {
                        String facultad = carreraData.get("facultad");
                        String carrera = carreraData.get("carrera");
                        String code = String.valueOf(carreraData.get("code"));
                        String code_facultad = String.valueOf(carreraData.get("code_facultad"));
                        // Verificamos si la facultad ya está en el mapa, si no, la agregamos
                        // Crear una instancia de Carrera y establecer los valores
                        
                        if (carreraService.findOne(Long.parseLong(code)) == null) {
                            Carrera nuevaCarrera = new Carrera();
                            Facultad facultad2 = facultadService.findOne(Long.valueOf(Integer.parseInt(code_facultad)));
                            nuevaCarrera.setId_carrera(Long.parseLong(code));
                            nuevaCarrera.setNom_carrera(carrera);
                            nuevaCarrera.setEstado("A");
                            nuevaCarrera.setFacultad(facultad2);
                            nuevaCarrera.setFecha_registro(new Date());
                            nuevaCarrera.setDescripcion(carrera);

                            // Guardar la nueva carrera en la base de datos
                            carreraService.save(nuevaCarrera);
                        } else {
                            Facultad facultad2 = facultadService.findOne(Long.valueOf(Integer.parseInt(code_facultad)));
                            Carrera nuevaCarrera = carreraService.findOne(Long.parseLong(code));
                            nuevaCarrera.setNom_carrera(carrera);
                            nuevaCarrera.setFacultad(facultad2);
                            nuevaCarrera.setDescripcion(carrera);

                            // Guardar la nueva carrera en la base de datos
                            carreraService.save(nuevaCarrera);
                        }

                        //System.out.println("  Carrera: " + carrera);
                    }
                } else {
                    System.out.println("No se encontraron datos de facultades.");
                }
            } else {
                System.out.println("La respuesta del API es nula.");
            }
        } else {
            System.out.println("La solicitud al API no fue exitosa (código de estado: " + resp.getStatusCode() + ")");
        }

        System.out.println("SE CARGO LAS CARRERAS ");

        return "redirect:/carrera/formulario";
    }

}
