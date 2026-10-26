package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.entity.Proveedor;
import com.brasasdoradas.sistema.service.ProveedorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProveedorController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProveedorService proveedorService;

    @Test
    void listarProveedores_debeResponder200YMostrarLista() throws Exception {
        when(proveedorService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/proveedores"))
                .andExpect(status().isOk())
                .andExpect(view().name("proveedores/lista"))
                .andExpect(model().attributeExists("proveedores"));
    }

    @Test
    void nuevoFormulario_debeResponder200() throws Exception {
        mockMvc.perform(get("/proveedores/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("proveedores/formulario"))
                .andExpect(model().attributeExists("proveedor"));
    }

    @Test
    void guardar_conDatosValidos_debeRedirigirAListaDeProveedores() throws Exception {
        when(proveedorService.guardar(any(Proveedor.class))).thenReturn(new Proveedor());

        mockMvc.perform(post("/proveedores/guardar")
                        .param("razonSocial", "Distribuidora Los Andes SAC")
                        .param("ruc", "20123456789"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/proveedores"));

        verify(proveedorService, times(1)).guardar(any(Proveedor.class));
    }

    @Test
    void guardar_sinRazonSocial_debeMostrarFormularioConError() throws Exception {
        mockMvc.perform(post("/proveedores/guardar")
                        .param("razonSocial", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("proveedores/formulario"));

        verify(proveedorService, never()).guardar(any(Proveedor.class));
    }

    @Test
    void eliminarProveedor_debeRedirigirAListaDeProveedores() throws Exception {
        mockMvc.perform(get("/proveedores/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/proveedores"));

        verify(proveedorService, times(1)).eliminar(1);
    }

    @Test
    void eliminarProveedor_conIdInvalido_debeInvocarServicio() throws Exception {
        mockMvc.perform(get("/proveedores/eliminar/999"))
                .andExpect(status().is3xxRedirection());

        verify(proveedorService, times(1)).eliminar(999);
    }
}