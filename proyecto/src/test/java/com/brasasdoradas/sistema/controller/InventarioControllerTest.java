package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.entity.MovimientoInventario;
import com.brasasdoradas.sistema.entity.Producto;
import com.brasasdoradas.sistema.repository.ProveedorRepository;
import com.brasasdoradas.sistema.service.MovimientoInventarioService;
import com.brasasdoradas.sistema.service.ProductoService;
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

@WebMvcTest(InventarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimientoInventarioService movimientoService;
    @MockBean
    private ProductoService productoService;
    @MockBean
    private ProveedorRepository proveedorRepository;

    @Test
    void listar_debeResponder200YMostrarMovimientos() throws Exception {
        when(movimientoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/inventario"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/lista"))
                .andExpect(model().attributeExists("movimientos"));
    }

    @Test
    void nuevoForm_debeResponder200YCargarCatalogos() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of());
        when(proveedorRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/inventario/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/formulario"))
                .andExpect(model().attributeExists("productos", "proveedores", "tipos"));
    }

    @Test
    void registrar_movimientoValido_debeRedirigirAListaDeInventario() throws Exception {
        Producto producto = Producto.builder().idProducto(1).nombreProducto("Pollo Entero").stock(10).build();
        when(productoService.buscarPorId(1)).thenReturn(producto);
        when(movimientoService.registrarMovimiento(any(MovimientoInventario.class)))
                .thenReturn(new MovimientoInventario());

        mockMvc.perform(post("/inventario/registrar")
                        .param("idProducto", "1")
                        .param("tipoMovimiento", "INGRESO")
                        .param("cantidad", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inventario"));

        verify(movimientoService, times(1)).registrarMovimiento(any(MovimientoInventario.class));
    }

    @Test
    void registrar_conStockInsuficiente_debeMostrarFormularioConError() throws Exception {
        Producto producto = Producto.builder().idProducto(2).nombreProducto("Jarra de Chicha").stock(1).build();
        when(productoService.buscarPorId(2)).thenReturn(producto);
        when(productoService.listarTodos()).thenReturn(List.of());
        when(proveedorRepository.findAll()).thenReturn(List.of());
        when(movimientoService.registrarMovimiento(any(MovimientoInventario.class)))
                .thenThrow(new IllegalStateException("Stock insuficiente para registrar la salida de: Jarra de Chicha"));

        mockMvc.perform(post("/inventario/registrar")
                        .param("idProducto", "2")
                        .param("tipoMovimiento", "SALIDA")
                        .param("cantidad", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/formulario"))
                .andExpect(model().attributeExists("error"));
    }
}
