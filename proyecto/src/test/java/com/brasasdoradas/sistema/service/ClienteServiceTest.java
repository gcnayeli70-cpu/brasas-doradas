package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.Cliente;
import com.brasasdoradas.sistema.repository.ClienteRepository;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void listarTodos_debeRetornarListaDeClientes() {
        Cliente cliente = Cliente.builder().idCliente(1).nombres("Nayeli").apellidos("Gala").build();
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<Cliente> resultado = clienteService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Nayeli", resultado.get(0).getNombres());
    }

    @Test
    void buscarPorId_clienteExistente_debeRetornarCliente() {
        Cliente cliente = Cliente.builder().idCliente(1).nombres("Nayeli").build();
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.buscarPorId(1);

        assertEquals("Nayeli", resultado.getNombres());
    }

    @Test
    void buscarPorId_clienteInexistente_debeLanzarExcepcion() {
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class,
                () -> clienteService.buscarPorId(99));
    }

    @Test
    void guardar_debeDelegarAlRepositorio() {
        Cliente cliente = Cliente.builder().nombres("Carlos").build();
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente resultado = clienteService.guardar(cliente);

        assertEquals("Carlos", resultado.getNombres());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void eliminar_debeDelegarAlRepositorio() {
        clienteService.eliminar(1);
        verify(clienteRepository, times(1)).deleteById(1);
    }
}