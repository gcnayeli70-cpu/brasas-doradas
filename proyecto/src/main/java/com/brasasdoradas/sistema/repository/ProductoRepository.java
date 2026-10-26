package com.brasasdoradas.sistema.repository;

import com.brasasdoradas.sistema.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByEstado(Producto.EstadoProducto estado);
    List<Producto> findByCategoria_IdCategoria(Integer idCategoria);
}
