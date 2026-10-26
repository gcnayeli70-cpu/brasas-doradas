package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.entity.Mesa;
import com.brasasdoradas.sistema.entity.Salon;
import com.brasasdoradas.sistema.repository.SalonRepository;
import com.brasasdoradas.sistema.service.MesaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MesaController.class)
@AutoConfigureMockMvc(addFilters = false)
class MesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MesaService mesaService;

    @MockBean
    private SalonRepository salonRepository;

    @Test
    void listarMesas_debeResponder200YMostrarLista() throws Exception {
        when(mesaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/mesas"))
                .andExpect(status().isOk())
                .andExpect(view().name("mesas/lista"))
                .andExpect(model().attributeExists("mesas"));
    }

    @Test
    void nuevaFormulario_debeResponder200() throws Exception {
        when(salonRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/mesas/nueva"))
                .andExpect(status().isOk())
                .andExpect(view().name("mesas/formulario"));
    }

    @Test
    void cambiarEstado_debeRedirigirAListaDeMesas() throws Exception {
        mockMvc.perform(get("/mesas/estado/1/OCUPADA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mesas"));

        verify(mesaService, times(1)).cambiarEstado(1, Mesa.EstadoMesa.OCUPADA);
    }

    @Test
    void guardar_conSalonValido_debeRedirigirAListaDeMesas() throws Exception {
        Salon salon = Salon.builder().idSalon(1).nombreSalon("Terraza").build();
        when(salonRepository.findById(1)).thenReturn(Optional.of(salon));
        when(mesaService.guardar(any(Mesa.class))).thenReturn(new Mesa());

        mockMvc.perform(post("/mesas/guardar")
                        .param("numeroMesa", "5")
                        .param("idSalon", "1")
                        .param("capacidad", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mesas"));

        verify(mesaService, times(1)).guardar(any(Mesa.class));
    }

    @Test
    void guardar_conSalonInexistente_debeLanzarExcepcion() {
        when(salonRepository.findById(99)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () ->
                mockMvc.perform(post("/mesas/guardar")
                        .param("numeroMesa", "5")
                        .param("idSalon", "99")
                        .param("capacidad", "4")));
    }
}