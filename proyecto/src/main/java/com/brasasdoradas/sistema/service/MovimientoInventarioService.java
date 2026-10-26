package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.MovimientoInventario;
import com.brasasdoradas.sistema.entity.Producto;
import com.brasasdoradas.sistema.repository.MovimientoInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoService productoService;

    public List<MovimientoInventario> listarTodos() {
        return movimientoRepository.findAll();
    }

    @Transactional
    public MovimientoInventario registrarMovimiento(MovimientoInventario movimiento) {
        Producto producto = movimiento.getProducto();

        switch (movimiento.getTipoMovimiento()) {
            case INGRESO -> producto.setStock(producto.getStock() + movimiento.getCantidad());
            case SALIDA -> {
                if (producto.getStock() < movimiento.getCantidad()) {
                    throw new IllegalStateException(
                            "Stock insuficiente para registrar la salida de: " + producto.getNombreProducto());
                }
                producto.setStock(producto.getStock() - movimiento.getCantidad());
            }
            case AJUSTE -> producto.setStock(movimiento.getCantidad());
        }

        productoService.guardar(producto);
        return movimientoRepository.save(movimiento);
    }
}