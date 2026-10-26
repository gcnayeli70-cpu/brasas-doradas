package com.brasasdoradas.sistema.repository;

import com.brasasdoradas.sistema.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    Optional<Empleado> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
}
