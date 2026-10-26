package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.Mesa;
import com.brasasdoradas.sistema.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;

    public List<Mesa> listarTodas() {
        return mesaRepository.findAll();
    }

    public List<Mesa> listarDisponibles() {
        return mesaRepository.findByEstado(Mesa.EstadoMesa.DISPONIBLE);
    }

    public Mesa buscarPorId(Integer id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mesa no encontrada con id: " + id));
    }

    public Mesa guardar(Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    public Mesa cambiarEstado(Integer idMesa, Mesa.EstadoMesa nuevoEstado) {
        Mesa mesa = buscarPorId(idMesa);
        mesa.setEstado(nuevoEstado);
        return mesaRepository.save(mesa);
    }
}
