package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.Empleado;
import com.brasasdoradas.sistema.entity.Rol;
import com.brasasdoradas.sistema.repository.EmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Pruebas del modulo de autenticacion (CP-006 del informe QA: restriccion de
 * accesos por perfil de seguridad). Verifica que el username y los roles
 * declarados en la base de datos se traduzcan correctamente al formato
 * ROLE_XXX que exige Spring Security, y que los empleados inactivos no
 * puedan autenticarse.
 */
@ExtendWith(MockitoExtension.class)
class EmpleadoDetailsServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoDetailsService empleadoDetailsService;

    @Test
    void loadUserByUsername_empleadoActivo_debeRetornarUserDetailsConRolCorrecto() {
        Rol rol = Rol.builder().idRol(1).nombreRol("ADMINISTRADOR").build();
        Empleado empleado = Empleado.builder()
                .idEmpleado(1)
                .usuario("admin1")
                .contrasena("$2a$10$hashDeEjemplo")
                .rol(rol)
                .estado(Empleado.EstadoEmpleado.ACTIVO)
                .build();
        when(empleadoRepository.findByUsuario("admin1")).thenReturn(Optional.of(empleado));

        UserDetails resultado = empleadoDetailsService.loadUserByUsername("admin1");

        assertEquals("admin1", resultado.getUsername());
        assertTrue(resultado.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRADOR")));
    }

    @Test
    void loadUserByUsername_usuarioInexistente_debeLanzarExcepcion() {
        when(empleadoRepository.findByUsuario("desconocido")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> empleadoDetailsService.loadUserByUsername("desconocido"));
    }

    @Test
    void loadUserByUsername_empleadoInactivo_debeLanzarExcepcion() {
        Rol rol = Rol.builder().idRol(3).nombreRol("MOZO").build();
        Empleado empleado = Empleado.builder()
                .idEmpleado(2)
                .usuario("mozo1")
                .contrasena("hash")
                .rol(rol)
                .estado(Empleado.EstadoEmpleado.INACTIVO)
                .build();
        when(empleadoRepository.findByUsuario("mozo1")).thenReturn(Optional.of(empleado));

        assertThrows(UsernameNotFoundException.class,
                () -> empleadoDetailsService.loadUserByUsername("mozo1"));
    }

    @Test
    void loadUserByUsername_rolCajero_debeMapearAlAuthorityCorrecto() {
        Rol rol = Rol.builder().idRol(2).nombreRol("CAJERO").build();
        Empleado empleado = Empleado.builder()
                .idEmpleado(3)
                .usuario("cajero1")
                .contrasena("hash")
                .rol(rol)
                .estado(Empleado.EstadoEmpleado.ACTIVO)
                .build();
        when(empleadoRepository.findByUsuario("cajero1")).thenReturn(Optional.of(empleado));

        UserDetails resultado = empleadoDetailsService.loadUserByUsername("cajero1");

        assertTrue(resultado.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CAJERO")));
    }
}
