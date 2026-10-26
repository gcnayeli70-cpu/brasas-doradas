# Sistema de Gestión de Ventas - Pollería Brasas Doradas

Proyecto Spring Boot 3 + Java 17 + MySQL + Thymeleaf, basado en el informe de
"Calidad y Pruebas de Software".

## 1. Requisitos previos
- IntelliJ IDEA (Community o Ultimate)
- JDK 17 instalado
- MySQL Server corriendo (ya creaste la base de datos `brasas_doradas_db` con el script SQL)
- Maven (viene integrado en IntelliJ, no necesitas instalarlo aparte)

## 2. Abrir el proyecto en IntelliJ
1. Descomprime este ZIP en una carpeta, por ejemplo: `C:\proyectos\brasas-doradas`
2. Abre IntelliJ IDEA → **File → Open** → selecciona la carpeta `brasas-doradas`
3. IntelliJ detectará el `pom.xml` y descargará automáticamente todas las
   dependencias de Maven (Spring Boot, MySQL Connector, Thymeleaf, etc.).
   Esto puede tardar unos minutos la primera vez.

## 3. Configurar la conexión a MySQL
Abre el archivo:
```
src/main/resources/application.properties
```
Y cambia esta línea con tu contraseña real de MySQL Workbench:
```properties
spring.datasource.password=TU_CONTRASENA_AQUI
```

Si tu base de datos usa otro puerto o usuario, ajusta también:
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/brasas_doradas_db?...
spring.datasource.username=root
```

## 4. Usuarios de prueba (ya funcionan, no requieren pasos extra)
`src/main/resources/data.sql` inserta automáticamente 3 empleados con
contraseñas BCrypt **ya válidas** apenas Hibernate crea las tablas
(`spring.jpa.defer-datasource-initialization=true` + `spring.sql.init.mode=always`
en `application.properties`). Puedes iniciar sesión directamente con:

| Usuario   | Contraseña | Rol            |
|-----------|------------|----------------|
| `admin`   | `admin123` | ADMINISTRADOR  |
| `cajero1` | `admin123` | CAJERO         |
| `mozo1`   | `admin123` | MOZO           |

⚠️ Cambia estas credenciales antes de cualquier despliegue real; están
pensadas solo para desarrollo/demo y para las pruebas de carga con JMeter
(sección 9).

## 5. Ejecutar el proyecto
En IntelliJ, abre la clase:
```
src/main/java/com/brasasdoradas/sistema/SistemaVentasApplication.java
```
Y dale clic en el botón ▶ (Run) al lado del método `main`.

Cuando veas en la consola algo como:
```
Tomcat started on port 8080
```
Abre tu navegador en: **http://localhost:8080/login**

## 6. Ejecutar las pruebas unitarias (JUnit 5 + Mockito)
En el panel de proyecto, ve a:
```
src/test/java/com/brasasdoradas/sistema/service/PedidoServiceTest.java
```
Clic derecho → **Run 'PedidoServiceTest'**. Esta prueba valida exactamente
el cálculo del IGV (18%) descrito en el informe, incluyendo el caso del
defecto crítico BUG-001 (error de precisión decimal).

## 7. Módulos incluidos
- Login con Spring Security (basado en la tabla `empleados` y roles)
- Gestión de Productos (CRUD)
- Gestión de Clientes (CRUD)
- Gestión de Mesas (estado disponible/ocupada)
- Registro de Pedidos con detalle multi-línea y cálculo automático de
  subtotal, IGV y total
- Prueba unitaria de la lógica de IGV con JUnit 5 + Mockito

## 8. Estructura del proyecto
```
entity/        -> 13 entidades JPA (una por tabla de la base de datos)
repository/     -> Interfaces Spring Data JPA
service/        -> Lógica de negocio (cálculo de IGV, stock, autenticación)
controller/     -> Controladores MVC (rutas web)
config/         -> Configuración de Spring Security
resources/templates/ -> Vistas Thymeleaf + Bootstrap
resources/data.sql   -> Datos semilla (roles, empleados, mesas, productos)
pruebas-rendimiento/ -> Plan de pruebas JMeter (Capítulo IV.5 del informe)
Dockerfile, docker-compose.yml -> Contenerización (Capítulo V.5 del informe)
```

## 9. Ejecutar todo con Docker (app + MySQL, sin instalar nada localmente)
Requiere solo tener Docker y Docker Compose instalados.

```bash
docker compose up --build
```

Esto levanta dos contenedores:
- **db**: MySQL 8.0, con un volumen persistente (`brasas_doradas_mysql_data`)
  para que los datos no se pierdan al reiniciar.
- **app**: compila el proyecto con Maven dentro de la imagen (multi-stage
  build) y ejecuta el jar final con un JRE 17 liviano (Alpine).

La app espera automáticamente a que MySQL esté saludable
(`healthcheck` en `docker-compose.yml`) antes de arrancar, y crea/actualiza
el esquema con `spring.jpa.hibernate.ddl-auto=update` + siembra los datos de
`data.sql` en el primer arranque.

Una vez que veas `Tomcat started on port 8080` en los logs, abre:
**http://localhost:8080/login** (usa las credenciales de la sección 4).

Para bajar los contenedores (conservando los datos):
```bash
docker compose down
```
Para bajarlos y borrar también los datos de MySQL:
```bash
docker compose down -v
```

> Nota: `docker-compose.yml` usa las mismas credenciales de MySQL para
> desarrollo local (`root` / `admin123`). Cámbialas por variables de entorno
> reales si esto se despliega fuera de un entorno de práctica.

## 10. Ejecutar las pruebas de rendimiento con JMeter (Capítulo IV.5)
El informe menciona pruebas de carga con Apache JMeter pero el archivo de
plan de pruebas no estaba incluido en la entrega original. Ahora sí está en
`pruebas-rendimiento/BrasasDoradas-PruebaCarga.jmx`.

**Qué simula:** 50 usuarios concurrentes (mozos/cajeros) que inician sesión
(manejando el token CSRF real que exige Spring Security) y navegan por los
módulos críticos (`/pedidos`, `/productos`, `/mesas`, `/reportes`), 10
iteraciones cada uno, con una pausa aleatoria entre acciones para simular
tiempo de uso real.

Pasos para ejecutarlo:
1. Instala [Apache JMeter](https://jmeter.apache.org/download_jmeter.cgi)
   (o usa la imagen Docker `justb4/jmeter`).
2. Levanta la aplicación (sección 9, o localmente con IntelliJ).
3. Desde la carpeta `pruebas-rendimiento/`, ejecuta en modo consola (no GUI,
   recomendado para mediciones reales):
   ```bash
   jmeter -n -t BrasasDoradas-PruebaCarga.jmx -l resultados/resumen.jtl -e -o resultados/reporte-html
   ```
4. Revisa `resultados/reporte-html/index.html` para ver tiempos de respuesta
   promedio, percentiles (p90/p95), rendimiento (throughput) y tasa de error
   — las mismas métricas que el informe reporta en la Tabla 5, pero ahora
   con evidencia real y reproducible en vez de una cifra sin respaldo.
5. Si `admin123` deja de ser la contraseña sembrada (por ejemplo si editaste
   `data.sql`), actualiza las variables `USUARIO`/`CLAVE` del plan desde la
   GUI de JMeter o con `-JUSUARIO=... -JCLAVE=...` al ejecutar.
