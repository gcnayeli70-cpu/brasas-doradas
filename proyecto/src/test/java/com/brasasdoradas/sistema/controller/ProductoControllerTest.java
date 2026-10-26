package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.entity.Categoria;
import com.brasasdoradas.sistema.entity.Producto;
import com.brasasdoradas.sistema.repository.CategoriaRepository;
import com.brasasdoradas.sistema.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas con Spring MockMvc del controlador de Productos.
 * Simulan peticiones HTTP GET hacia las rutas del backend,
 * verificando codigos de estado 200 OK y 302 Redirect,
 * tal como se describe en el Capitulo III del informe (Spring MockMvc).
 */
@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private CategoriaRepository categoriaRepository;

    @Test
    void listarProductos_debeResponder200YMostrarTablaConDatos() throws Exception {
        Categoria categoria = Categoria.builder().idCategoria(1).nombreCategoria("Pollos a la Brasa").build();
        Producto producto = Producto.builder()
                .idProducto(1)
                .nombreProducto("1/4 de Pollo con Papas y Ensalada")
                .precio(new BigDecimal("18.00"))
                .categoria(categoria)
                .stock(50)
                .build();

        when(productoService.listarTodos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/lista"))
                .andExpect(model().attributeExists("productos"));
    }

    @Test
    void nuevoFormulario_debeResponder200YMostrarFormularioVacio() throws Exception {
        when(categoriaRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/productos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"));
    }

    @Test
    void eliminarProducto_debeRedirigirConCodigo302AListaDeProductos() throws Exception {
        mockMvc.perform(get("/productos/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(productoService, times(1)).eliminar(1);
    }

    @Test
    void editarFormulario_conProductoExistente_debeResponder200() throws Exception {
        Categoria categoria = Categoria.builder().idCategoria(1).nombreCategoria("Pollos a la Brasa").build();
        Producto producto = Producto.builder()
                .idProducto(1)
                .nombreProducto("1/4 de Pollo con Papas y Ensalada")
                .precio(new BigDecimal("18.00"))
                .categoria(categoria)
                .stock(50)
                .build();

        when(productoService.buscarPorId(1)).thenReturn(producto);
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/productos/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attributeExists("producto", "categorias"));
    }

    @Test
    void guardar_conCategoriaValida_debeRedirigirAListaDeProductos() throws Exception {
        Categoria categoria = Categoria.builder().idCategoria(1).nombreCategoria("Pollos a la Brasa").build();
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(productoService.guardar(any(Producto.class))).thenReturn(new Producto());

        mockMvc.perform(post("/productos/guardar")
                        .param("nombreProducto", "1/4 de Pollo")
                        .param("precio", "18.00")
                        .param("idCategoria", "1")
                        .param("stock", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(productoService, times(1)).guardar(any(Producto.class));
    }
}