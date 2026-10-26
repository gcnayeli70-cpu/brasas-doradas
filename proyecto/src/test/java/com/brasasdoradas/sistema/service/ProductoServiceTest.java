package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.Categoria;
import com.brasasdoradas.sistema.entity.Producto;
import com.brasasdoradas.sistema.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        Categoria categoria = Categoria.builder().idCategoria(1).nombreCategoria("Pollos a la Brasa").build();
        producto = Producto.builder()
                .idProducto(1)
                .nombreProducto("1/4 de Pollo")
                .precio(new BigDecimal("18.00"))
                .categoria(categoria)
                .stock(10)
                .estado(Producto.EstadoProducto.ACTIVO)
                .build();
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.listarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void listarActivos_debeFiltrarPorEstadoActivo() {
        when(productoRepository.findByEstado(Producto.EstadoProducto.ACTIVO)).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.listarActivos();

        assertEquals(1, resultado.size());
        assertEquals(Producto.EstadoProducto.ACTIVO, resultado.get(0).getEstado());
    }

    @Test
    void buscarPorId_productoInexistente_debeLanzarExcepcion() {
        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productoService.buscarPorId(99));
    }

    @Test
    void descontarStock_conStockSuficiente_debeDescontarCorrectamente() {
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        productoService.descontarStock(1, 3);

        assertEquals(7, producto.getStock());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void descontarStock_sinStockSuficiente_debeLanzarExcepcion() {
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        assertThrows(IllegalStateException.class, () -> productoService.descontarStock(1, 999));
    }
}