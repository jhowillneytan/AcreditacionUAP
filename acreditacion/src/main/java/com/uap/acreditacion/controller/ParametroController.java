package com.uap.acreditacion.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.entity.Cargo;
import com.uap.acreditacion.entity.Parametro;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.service.ICargoService;
import com.uap.acreditacion.service.IParametroService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.IRequisitoService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
public class ParametroController {

    @Autowired
    private ITipoPersonaService tipoPersonaService;

    @Autowired
    private IPersonaService personaService;

    @Autowired
    private IParametroService parametroService;

    @Autowired
    private IRequisitoService requisitoService;

    @GetMapping("/form-parametro")
    public String formCargo(ModelMap model, HttpServletRequest request) {
        if (request.getSession().getAttribute("persona") != null) {
            Persona p2 = (Persona) request.getSession().getAttribute("persona");
            Persona p = personaService.findOne(p2.getId_persona());
            model.addAttribute("personasession", p);
            model.addAttribute("tipoPersonasession",
                    tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
            List<Parametro> listpParametros = parametroService.findAll();
            model.addAttribute("parametro", new Parametro());
            model.addAttribute("parametros", listpParametros);
            model.addAttribute("requisitos", requisitoService.findAll());
            model.addAttribute("opcionParametro", "true");
            model.addAttribute("subMenuSeleccionado", "true");
            return "/Parametro/formulario";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/RegistrarParametro")
    public String agregarParametro(RedirectAttributes redirectAttrs, @Validated Parametro parametro) {
        parametro.setEstado("A");
        parametroService.save(parametro);
        redirectAttrs
                .addFlashAttribute("mensaje", "Agregado correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/form-parametro";
    }
}
