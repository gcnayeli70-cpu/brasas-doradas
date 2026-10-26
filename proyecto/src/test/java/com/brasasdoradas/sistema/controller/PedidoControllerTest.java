package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.repository.ClienteRepository;
import com.brasasdoradas.sistema.repository.EmpleadoRepository;
import com.brasasdoradas.sistema.service.MesaService;
import com.brasasdoradas.sistema.service.PedidoService;
import com.brasasdoradas.sistema.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;
    @MockBean
    private MesaService mesaService;
    @MockBean
    private ProductoService productoService;
    @MockBean
    private ClienteRepository clienteRepository;
    @MockBean
    private EmpleadoRepository empleadoRepository;

    @Test
    void listarPedidos_debeResponder200() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedidos/lista"))
                .andExpect(model().attributeExists("pedidos"));
    }

    @Test
    void nuevoFormulario_debeResponder200() throws Exception {
        when(mesaService.listarDisponibles()).thenReturn(List.of());
        when(clienteRepository.findAll()).thenReturn(List.of());
        when(productoService.listarActivos()).thenReturn(List.of());

        mockMvc.perform(get("/pedidos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedidos/formulario"));
    }
}