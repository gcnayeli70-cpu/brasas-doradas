package com.brasasdoradas.sistema.repository;

import com.brasasdoradas.sistema.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MesaRepository extends JpaRepository<Mesa, Integer> {
    List<Mesa> findByEstado(Mesa.EstadoMesa estado);
}
