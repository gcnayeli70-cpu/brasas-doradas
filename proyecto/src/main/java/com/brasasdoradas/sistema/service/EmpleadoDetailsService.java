package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.Empleado;
import com.brasasdoradas.sistema.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoDetailsService implements UserDetailsService {

    private final EmpleadoRepository empleadoRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        Empleado empleado = empleadoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + usuario));

        if (empleado.getEstado() == Empleado.EstadoEmpleado.INACTIVO) {
            throw new UsernameNotFoundException("El usuario esta inactivo: " + usuario);
        }

        String rolSpring = "ROLE_" + empleado.getRol().getNombreRol(); // ej. ROLE_ADMINISTRADOR

        return new User(
                empleado.getUsuario(),
                empleado.getContrasena(), // debe estar almacenada con BCrypt
                List.of(new SimpleGrantedAuthority(rolSpring))
        );
    }
}