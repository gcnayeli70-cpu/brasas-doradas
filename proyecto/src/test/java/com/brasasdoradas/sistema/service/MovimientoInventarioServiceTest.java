package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.MovimientoInventario;
import com.brasasdoradas.sistema.entity.Producto;
import com.brasasdoradas.sistema.repository.MovimientoInventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Estas pruebas cubren la regla critica descrita en el informe QA (CP-004):
 * el sistema debe bloquear salidas de inventario cuando el stock es insuficiente,
 * y actualizar correctamente el stock en los tres tipos de movimiento.
 */
@ExtendWith(MockitoExtension.class)
class MovimientoInventarioServiceTest {

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private MovimientoInventarioService movimientoInventarioService;

    @Test
    void listarTodos_debeRetornarListaDeMovimientos() {
        MovimientoInventario movimiento = MovimientoInventario.builder().idMovimiento(1).build();
        when(movimientoRepository.findAll()).thenReturn(List.of(movimiento));

        List<MovimientoInventario> resultado = movimientoInventarioService.listarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void registrarMovimiento_tipoIngreso_debeIncrementarStock() {
        Producto producto = Producto.builder().idProducto(1).nombreProducto("Pollo Entero").stock(10).build();
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .tipoMovimiento(MovimientoInventario.TipoMovimiento.INGRESO)
                .cantidad(5)
                .build();
        when(movimientoRepository.save(movimiento)).thenReturn(movimiento);

        movimientoInventarioService.registrarMovimiento(movimiento);

        assertEquals(15, producto.getStock());
        verify(productoService, times(1)).guardar(producto);
        verify(movimientoRepository, times(1)).save(movimiento);
    }

    @Test
    void registrarMovimiento_tipoSalidaConStockSuficiente_debeDecrementarStock() {
        Producto producto = Producto.builder().idProducto(2).nombreProducto("Jarra de Chicha").stock(10).build();
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .tipoMovimiento(MovimientoInventario.TipoMovimiento.SALIDA)
                .cantidad(4)
                .build();
        when(movimientoRepository.save(movimiento)).thenReturn(movimiento);

        movimientoInventarioService.registrarMovimiento(movimiento);

        assertEquals(6, producto.getStock());
        verify(productoService, times(1)).guardar(producto);
    }

    @Test
    void registrarMovimiento_tipoSalidaConStockInsuficiente_debeLanzarExcepcion() {
        Producto producto = Producto.builder().idProducto(3).nombreProducto("Combo Familiar").stock(2).build();
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .tipoMovimiento(MovimientoInventario.TipoMovimiento.SALIDA)
                .cantidad(5)
                .build();

        assertThrows(IllegalStateException.class,
                () -> movimientoInventarioService.registrarMovimiento(movimiento));

        verify(movimientoRepository, never()).save(any());
        verify(productoService, never()).guardar(any());
    }

    @Test
    void registrarMovimiento_tipoAjuste_debeFijarStockExacto() {
        Producto producto = Producto.builder().idProducto(4).nombreProducto("Mostrito").stock(20).build();
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .tipoMovimiento(MovimientoInventario.TipoMovimiento.AJUSTE)
                .cantidad(7)
                .build();
        when(movimientoRepository.save(movimiento)).thenReturn(movimiento);

        movimientoInventarioService.registrarMovimiento(movimiento);

        assertEquals(7, producto.getStock());
    }
}
