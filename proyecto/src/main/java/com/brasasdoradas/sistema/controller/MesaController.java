package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.dto.MesaDTO;
import com.brasasdoradas.sistema.entity.Mesa;
import com.brasasdoradas.sistema.entity.Salon;
import com.brasasdoradas.sistema.repository.SalonRepository;
import com.brasasdoradas.sistema.service.MesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;
    private final SalonRepository salonRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mesas", mesaService.listarTodas());
        return "mesas/lista";
    }

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("mesa", new MesaDTO());
        model.addAttribute("salones", salonRepository.findAll());
        return "mesas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("mesa") MesaDTO mesaDTO) {
        mesaService.guardar(toEntity(mesaDTO));
        return "redirect:/mesas";
    }

    @GetMapping("/estado/{id}/{estado}")
    public String cambiarEstado(@PathVariable Integer id, @PathVariable Mesa.EstadoMesa estado) {
        mesaService.cambiarEstado(id, estado);
        return "redirect:/mesas";
    }

    private Mesa toEntity(MesaDTO dto) {
        Salon salon = salonRepository.findById(dto.getIdSalon())
                .orElseThrow(() -> new IllegalArgumentException("Salón no encontrado: " + dto.getIdSalon()));

        Mesa mesa = new Mesa();
        mesa.setIdMesa(dto.getIdMesa());
        mesa.setNumeroMesa(dto.getNumeroMesa());
        mesa.setSalon(salon);
        if (dto.getCapacidad() != null) {
            mesa.setCapacidad(dto.getCapacidad());
        }
        if (dto.getEstado() != null) {
            mesa.setEstado(dto.getEstado());
        }
        return mesa;
    }
}