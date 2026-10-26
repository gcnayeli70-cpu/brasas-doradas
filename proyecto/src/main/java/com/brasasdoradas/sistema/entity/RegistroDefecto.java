package com.brasasdoradas.sistema.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "registro_defectos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegistroDefecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_defecto")
    private Integer idDefecto;

    @Column(name = "codigo_bug", nullable = false, unique = true, length = 20)
    private String codigoBug;

    @Column(name = "modulo_afectado", nullable = false, length = 80)
    private String moduloAfectado;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severidad severidad;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private EstadoDefecto estado = EstadoDefecto.ABIERTO;

    @Column(name = "fecha_reporte")
    private LocalDateTime fechaReporte;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @PrePersist
    public void prePersist() {
        if (fechaReporte == null) fechaReporte = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

    public enum Severidad { CRITICO, MODERADO, MENOR }
    public enum EstadoDefecto { ABIERTO, EN_CORRECCION, RESUELTO }
}