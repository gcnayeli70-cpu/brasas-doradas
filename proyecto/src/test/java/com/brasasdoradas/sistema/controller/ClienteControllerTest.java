package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.entity.Cliente;
import com.brasasdoradas.sistema.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Test
    void listarClientes_debeResponder200YMostrarLista() throws Exception {
        when(clienteService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/lista"))
                .andExpect(model().attributeExists("clientes"));
    }

    @Test
    void nuevoFormulario_debeResponder200() throws Exception {
        mockMvc.perform(get("/clientes/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/formulario"))
                .andExpect(model().attributeExists("cliente"));
    }

    @Test
    void editarFormulario_conClienteExistente_debeResponder200() throws Exception {
        Cliente cliente = Cliente.builder()
                .idCliente(1)
                .nombres("Juan")
                .apellidos("Perez")
                .dni("12345678")
                .build();

        when(clienteService.buscarPorId(1)).thenReturn(cliente);

        mockMvc.perform(get("/clientes/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/formulario"))
                .andExpect(model().attributeExists("cliente"));
    }

    @Test
    void editarFormulario_conClienteInexistente_debeLanzarExcepcion() {
        when(clienteService.buscarPorId(999))
                .thenThrow(new NoSuchElementException("Cliente no encontrado con id: 999"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/clientes/editar/999")));
    }

    @Test
    void guardar_conDatosValidos_debeRedirigirAListaDeClientes() throws Exception {
        when(clienteService.guardar(any(Cliente.class))).thenReturn(new Cliente());

        mockMvc.perform(post("/clientes/guardar")
                        .param("nombres", "Juan")
                        .param("apellidos", "Perez")
                        .param("dni", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"));

        verify(clienteService, times(1)).guardar(any(Cliente.class));
    }

    @Test
    void guardar_sinNombre_debeMostrarFormularioConError() throws Exception {
        mockMvc.perform(post("/clientes/guardar")
                        .param("nombres", "")
                        .param("apellidos", "Perez"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/formulario"));

        verify(clienteService, never()).guardar(any(Cliente.class));
    }

    @Test
    void eliminarCliente_debeRedirigirAListaDeClientes() throws Exception {
        mockMvc.perform(get("/clientes/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"));

        verify(clienteService, times(1)).eliminar(1);
    }
}