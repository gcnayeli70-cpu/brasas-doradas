package com.brasasdoradas.sistema.repository;

import com.brasasdoradas.sistema.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
    List<DetallePedido> findByPedido_IdPedido(Integer idPedido);

    /**
     * Productos mas vendidos (rotacion de platos), considerando solo pedidos PAGADOS.
     * Retorna: [0]=nombreProducto, [1]=unidadesVendidas, [2]=totalGenerado
     */
    @Query(value = "SELECT p.nombre_producto AS nombreProducto, " +
            "SUM(dp.cantidad) AS unidadesVendidas, " +
            "SUM(dp.subtotal_linea) AS totalGenerado " +
            "FROM detalle_pedidos dp " +
            "JOIN productos p ON dp.id_producto = p.id_producto " +
            "JOIN pedidos ped ON dp.id_pedido = ped.id_pedido " +
            "WHERE ped.estado = 'PAGADO' " +
            "GROUP BY p.id_producto, p.nombre_producto " +
            "ORDER BY unidadesVendidas DESC " +
            "LIMIT 10",
            nativeQuery = true)
    List<Object[]> obtenerProductosMasVendidos();
}
