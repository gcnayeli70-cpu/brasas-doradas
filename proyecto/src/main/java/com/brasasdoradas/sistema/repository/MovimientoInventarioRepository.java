package com.brasasdoradas.sistema.repository;

import com.brasasdoradas.sistema.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {
    List<MovimientoInventario> findByProducto_IdProducto(Integer idProducto);
}
