package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.Producto;
import com.brasasdoradas.sistema.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public List<Producto> listarActivos() {
        return productoRepository.findByEstado(Producto.EstadoProducto.ACTIVO);
    }

    public Producto buscarPorId(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con id: " + id));
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    /**
     * Descuenta stock de un producto al confirmarse un pedido.
     * Lanza excepcion si no hay stock suficiente (regla critica mencionada en el informe QA).
     */
    public void descontarStock(Integer idProducto, int cantidad) {
        Producto producto = buscarPorId(idProducto);
        if (producto.getStock() < cantidad) {
            throw new IllegalStateException(
                    "Stock insuficiente para el producto: " + producto.getNombreProducto());
        }
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }
}
