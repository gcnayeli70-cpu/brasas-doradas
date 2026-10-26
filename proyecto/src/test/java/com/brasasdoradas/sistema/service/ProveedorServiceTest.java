package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.Proveedor;
import com.brasasdoradas.sistema.repository.ProveedorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    @Test
    void listarTodos_debeRetornarListaDeProveedores() {
        Proveedor proveedor = Proveedor.builder().idProveedor(1).razonSocial("Distribuidora Los Andes SAC").build();
        when(proveedorRepository.findAll()).thenReturn(List.of(proveedor));

        List<Proveedor> resultado = proveedorService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Distribuidora Los Andes SAC", resultado.get(0).getRazonSocial());
    }

    @Test
    void buscarPorId_proveedorExistente_debeRetornarProveedor() {
        Proveedor proveedor = Proveedor.builder().idProveedor(1).razonSocial("Avicola Ica").build();
        when(proveedorRepository.findById(1)).thenReturn(Optional.of(proveedor));

        Proveedor resultado = proveedorService.buscarPorId(1);

        assertEquals("Avicola Ica", resultado.getRazonSocial());
    }

    @Test
    void buscarPorId_proveedorInexistente_debeLanzarExcepcion() {
        when(proveedorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> proveedorService.buscarPorId(99));
    }

    @Test
    void guardar_debeDelegarAlRepositorio() {
        Proveedor proveedor = Proveedor.builder().razonSocial("Nuevo Proveedor SAC").build();
        when(proveedorRepository.save(proveedor)).thenReturn(proveedor);

        Proveedor resultado = proveedorService.guardar(proveedor);

        assertEquals("Nuevo Proveedor SAC", resultado.getRazonSocial());
        verify(proveedorRepository, times(1)).save(proveedor);
    }

    @Test
    void eliminar_debeDelegarAlRepositorio() {
        proveedorService.eliminar(5);

        verify(proveedorRepository, times(1)).deleteById(5);
    }
}
