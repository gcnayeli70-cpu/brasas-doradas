-- ============================================================
-- Datos semilla minimos para que el sistema sea usable apenas
-- se levanta con Docker Compose (sin esto, la BD queda vacia
-- y nadie podria iniciar sesion). Usa INSERT IGNORE para que
-- sea seguro re-ejecutarlo en cada arranque del contenedor.
--
-- IMPORTANTE: las 3 cuentas de prueba usan la contrasena "admin123"
-- (hash BCrypt). Cambiar estas credenciales antes de un despliegue real.
-- ============================================================

INSERT IGNORE INTO roles (id_rol, nombre_rol, descripcion) VALUES
  (1, 'ADMINISTRADOR', 'Acceso total al sistema'),
  (2, 'CAJERO', 'Gestiona cobros, productos y reportes'),
  (3, 'MOZO', 'Registra pedidos y gestiona mesas');

INSERT IGNORE INTO empleados (id_empleado, nombres, apellidos, dni, telefono, id_rol, usuario, contrasena, estado, fecha_registro) VALUES
  (1, 'Administrador', 'Sistema', '00000001', '999999999', 1, 'admin',   '$2b$10$rkAHFzGZuDSMRAuf7by08eBxqZXZ.I63vR5nEB.1ZSwPf2VA47JMu', 'ACTIVO', NOW()),
  (2, 'Carlos', 'Ramirez',       '00000002', '999999998', 2, 'cajero1', '$2b$10$rkAHFzGZuDSMRAuf7by08eBxqZXZ.I63vR5nEB.1ZSwPf2VA47JMu', 'ACTIVO', NOW()),
  (3, 'Luis', 'Torres',          '00000003', '999999997', 3, 'mozo1',   '$2b$10$rkAHFzGZuDSMRAuf7by08eBxqZXZ.I63vR5nEB.1ZSwPf2VA47JMu', 'ACTIVO', NOW());

INSERT IGNORE INTO salones (id_salon, nombre_salon, descripcion) VALUES
  (1, 'Salon Principal', 'Area interior con aire acondicionado'),
  (2, 'Terraza', 'Area exterior techada');

INSERT IGNORE INTO mesas (id_mesa, numero_mesa, id_salon, capacidad, estado) VALUES
  (1, 1, 1, 4, 'DISPONIBLE'),
  (2, 2, 1, 4, 'DISPONIBLE'),
  (3, 3, 2, 6, 'DISPONIBLE'),
  (4, 4, 2, 2, 'DISPONIBLE');

INSERT IGNORE INTO categorias (id_categoria, nombre_categoria) VALUES
  (1, 'Pollos'),
  (2, 'Bebidas'),
  (3, 'Combos'),
  (4, 'Guarniciones');

INSERT IGNORE INTO productos (id_producto, nombre_producto, descripcion, precio, id_categoria, stock, estado) VALUES
  (1, 'Pollo Entero', 'Pollo a la brasa entero con papas y ensalada', 78.00, 1, 30, 'ACTIVO'),
  (2, '1/4 de Pollo', 'Cuarto de pollo con papas y ensalada', 22.00, 1, 40, 'ACTIVO'),
  (3, 'Jarra de Chicha Morada', 'Jarra de 1 litro', 12.00, 2, 25, 'ACTIVO'),
  (4, 'Gaseosa 1.5L', 'Inca Kola / Coca Cola', 10.00, 2, 25, 'ACTIVO'),
  (5, 'Combo Familiar', 'Pollo entero + papas + ensalada + gaseosa 1.5L', 95.00, 3, 15, 'ACTIVO'),
  (6, 'Papas Extra', 'Porcion adicional de papas fritas', 8.00, 4, 50, 'ACTIVO');
