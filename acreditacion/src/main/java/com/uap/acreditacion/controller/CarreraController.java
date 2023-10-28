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

import javax.servlet.http.HttpServletRequest;

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
        if (file.isEmpty()) {
            carrera.setFile(carrera2.getFile());
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
                    // Utilizamos un mapa para mapear facultades a listas de carreras
                    Map<String, List<String>> facultadesCarreras = new HashMap<>();

                    for (Map<String, String> carreraData : dataList) {
                        String facultad = carreraData.get("facultad");
                        String carrera = carreraData.get("carrera");

                        // Verificamos si la facultad ya está en el mapa, si no, la agregamos
                        if (!facultadesCarreras.containsKey(facultad)) {
                            facultadesCarreras.put(facultad, new ArrayList<>());
                        }

                        // Agregamos la carrera a la lista de carreras de la facultad
                        facultadesCarreras.get(facultad).add(carrera);
                    }

                    // Ahora tenemos un mapa de facultades y sus carreras asociadas
                    for (String facultad : facultadesCarreras.keySet()) {
                        System.out.println("Facultad: " + facultad);
                        List<String> carreras = facultadesCarreras.get(facultad);
                        for (Facultad facultad2 : facultads) {
                            if (facultad2.getNom_facultad().equals(facultad)) {
                                for (String carrera : carreras) {
                                    Carrera carrera2 = new Carrera();
                                    carrera2.setNom_carrera(carrera);
                                    carrera2.setFacultad(facultad2);
                                    carrera2.setFecha_registro(new Date());
                                    carrera2.setDescripcion(carrera);
                                    carreraService.save(carrera2);
                                    System.out.println("  Carrera: " + carrera);
                                }
                            }
                        }

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

        System.out.println("SE CARGO LAS MATERIAS ");

        return "redirect:/facultad/formulario";
    }

}
