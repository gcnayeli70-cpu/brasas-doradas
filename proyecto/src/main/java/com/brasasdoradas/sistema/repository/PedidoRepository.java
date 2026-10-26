package com.brasasdoradas.sistema.repository;

import com.brasasdoradas.sistema.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByEstado(Pedido.EstadoPedido estado);
    List<Pedido> findByMesa_IdMesa(Integer idMesa);

    @Query(value = "SELECT DATE(fecha_pedido) AS fecha, SUM(total) AS totalIngresos, COUNT(*) AS cantidadPedidos " +
            "FROM pedidos WHERE estado = 'PAGADO' GROUP BY DATE(fecha_pedido) ORDER BY fecha DESC",
            nativeQuery = true)
    List<Object[]> obtenerIngresosPorDia();

    @Query(value = "SELECT COUNT(*) AS totalPedidos, COALESCE(SUM(total), 0) AS totalFacturado, " +
            "COALESCE(AVG(total), 0) AS ticketPromedio FROM pedidos WHERE estado = 'PAGADO'",
            nativeQuery = true)
    List<Object[]> obtenerResumenGeneral();
}
