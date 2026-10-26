package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.Mesa;
import com.brasasdoradas.sistema.repository.MesaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MesaServiceTest {

    @Mock
    private MesaRepository mesaRepository;

    @InjectMocks
    private MesaService mesaService;

    @Test
    void listarTodas_debeRetornarListaDeMesas() {
        Mesa mesa = Mesa.builder().idMesa(1).numeroMesa(5).build();
        when(mesaRepository.findAll()).thenReturn(List.of(mesa));

        List<Mesa> resultado = mesaService.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals(5, resultado.get(0).getNumeroMesa());
    }

    @Test
    void listarDisponibles_debeFiltrarPorEstadoDisponible() {
        Mesa mesa = Mesa.builder().idMesa(1).numeroMesa(2).estado(Mesa.EstadoMesa.DISPONIBLE).build();
        when(mesaRepository.findByEstado(Mesa.EstadoMesa.DISPONIBLE)).thenReturn(List.of(mesa));

        List<Mesa> resultado = mesaService.listarDisponibles();

        assertEquals(1, resultado.size());
        assertEquals(Mesa.EstadoMesa.DISPONIBLE, resultado.get(0).getEstado());
        verify(mesaRepository, times(1)).findByEstado(Mesa.EstadoMesa.DISPONIBLE);
    }

    @Test
    void buscarPorId_mesaExistente_debeRetornarMesa() {
        Mesa mesa = Mesa.builder().idMesa(3).numeroMesa(7).build();
        when(mesaRepository.findById(3)).thenReturn(Optional.of(mesa));

        Mesa resultado = mesaService.buscarPorId(3);

        assertEquals(7, resultado.getNumeroMesa());
    }

    @Test
    void buscarPorId_mesaInexistente_debeLanzarExcepcion() {
        when(mesaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> mesaService.buscarPorId(99));
    }

    @Test
    void cambiarEstado_debeActualizarYPersistirLaMesa() {
        Mesa mesa = Mesa.builder().idMesa(1).numeroMesa(4).estado(Mesa.EstadoMesa.DISPONIBLE).build();
        when(mesaRepository.findById(1)).thenReturn(Optional.of(mesa));
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(inv -> inv.getArgument(0));

        Mesa resultado = mesaService.cambiarEstado(1, Mesa.EstadoMesa.OCUPADA);

        assertEquals(Mesa.EstadoMesa.OCUPADA, resultado.getEstado());
        verify(mesaRepository, times(1)).save(mesa);
    }

    @Test
    void guardar_debeDelegarAlRepositorio() {
        Mesa mesa = Mesa.builder().numeroMesa(10).build();
        when(mesaRepository.save(mesa)).thenReturn(mesa);

        Mesa resultado = mesaService.guardar(mesa);

        assertEquals(10, resultado.getNumeroMesa());
        verify(mesaRepository, times(1)).save(mesa);
    }
}
