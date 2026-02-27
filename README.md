# 🏗️ BuildLand - BackEnd

API REST desarrollada con **Spring Boot 3.5** para la gestión de presupuestos, cotizaciones, productos y usuarios en el rubro de la construcción.

---

## 📋 Tabla de Contenidos

- [Tecnologías](#-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración](#-configuración)
- [Ejecución](#-ejecución)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Seguridad y Autenticación](#-seguridad-y-autenticación)
- [Roles y Permisos](#-roles-y-permisos)
- [Tests](#-tests)

---

## 🛠️ Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.6 |
| Spring Security + OAuth2 Resource Server | — |
| Spring Data JPA (Hibernate) | — |
| SQL Server (MSSQL) | — |
| Apache Kafka | — |
| Lombok | — |
| JWT (JSON Web Tokens) | — |
| Spring Mail | — |
| Maven | — |
| JUnit 5 | — |

---

## ✅ Requisitos Previos

- **Java 21** o superior
- **Maven 3.8+** (o usar el wrapper `mvnw` incluido)
- **SQL Server** con una base de datos llamada `BuildLandDB`
- **Apache Kafka** (si se utilizan las funcionalidades de mensajería)
- Un servidor SMTP configurado (por defecto Gmail) para el envío de correos

---

## ⚙️ Configuración

Editar el archivo `src/main/resources/application.properties` con los datos de tu entorno:

```properties
# Base de datos
spring.datasource.url=jdbc:sqlserver://<HOST>:1433;databaseName=BuildLandDB;encrypt=true;trustServerCertificate=true;
spring.datasource.username=<USUARIO_DB>
spring.datasource.password=<PASSWORD_DB>

# JWT
jwt.key=<CLAVE_SECRETA_BASE64>

# Cookie de sesión
cookie.name=cookieBuildLand
cookie.expiresIn=900

# Email (SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<TU_EMAIL>
spring.mail.password=<TU_APP_PASSWORD>
```

> ⚠️ **Importante:** No subir credenciales reales al repositorio. Utilizar variables de entorno o un archivo `.env` para datos sensibles.

---

## 🚀 Ejecución

### Con Maven Wrapper

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

### Con Maven instalado

```bash
mvn spring-boot:run
```

### Compilar y ejecutar el JAR

```bash
.\mvnw.cmd clean package -DskipTests
java -jar target/BuildLand-1.0.0.jar
```

La aplicación se levanta por defecto en: **http://localhost:8080**

---

## 🏛️ Arquitectura del Proyecto

```
src/main/java/com/uai/BuildLand/
├── BuildLandApplication.java          # Clase principal
├── config/                            # Configuración de seguridad, JWT, filtros y tareas programadas
│   ├── JwtConfig.java
│   ├── JwtFromCookieFilter.java
│   ├── PasswordEncoder.java
│   ├── SecurityConfig.java
│   └── VerificationCodeScheduledTasks.java
├── controllers/                       # Controladores REST
│   ├── AuthController.java
│   ├── CategoriaController.java
│   ├── CotizacionController.java
│   ├── DistribuidoraController.java
│   ├── EmpleadoController.java
│   ├── GrupoController.java
│   ├── MarcaController.java
│   ├── PermisoController.java
│   ├── PersonaFisicaController.java
│   ├── PresupuestoController.java
│   ├── ProductoController.java
│   ├── ReporteController.java
│   ├── UsuarioController.java
│   └── specifications/               # Specifications para filtros dinámicos JPA
├── models/                            # Entidades JPA y DTOs
│   ├── access/                        # Grupos y Permisos
│   ├── auth/                          # Usuarios y autenticación
│   ├── cotizacion/                    # Cotizaciones y detalles
│   ├── dto/                           # Data Transfer Objects
│   ├── presupuesto/                   # Presupuestos y detalles
│   └── reporte/                       # Modelos de reportes
├── repositories/                      # Repositorios Spring Data JPA
└── services/                          # Lógica de negocio
    ├── AuthService.java
    ├── AuthUserDetailsService.java
    ├── EmailService.java
    ├── GrupoSubgrupoService.java
    ├── JwtTokenService.java
    └── VerificationCodeService.java
```

---

## 📡 Endpoints de la API

Todos los endpoints se encuentran bajo el prefijo `/api`.

### 🔐 Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `POST` | `/auth/token` | Iniciar sesión (login) | Público |
| `POST` | `/auth/restorePassword` | Solicitar restauración de contraseña | Público |
| `POST` | `/auth/verifyPasswordCode` | Verificar código de restauración | Público |
| `POST` | `/auth/changePassword` | Cambiar contraseña | Público |
| `POST` | `/auth/logout` | Cerrar sesión | Autenticado |

### 👤 Usuarios (`/api/usuarios`)

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `GET` | `/usuarios` | Obtener usuario autenticado | PF, Distribuidora, Admin |
| `GET` | `/usuarios/cuit/{cuit}` | Buscar usuario por CUIT | PF, Distribuidora, Admin |
| `GET` | `/usuarios/email` | Obtener email del usuario actual | Autenticado |
| `GET` | `/usuarios/email/{email}` | Buscar usuario por email | Admin |
| `GET` | `/usuarios/login` | Info de login del usuario | Autenticado |
| `POST` | `/usuarios` | Crear usuario | Permiso A-Us / Admin |
| `PATCH` | `/usuarios` | Modificar usuario | Permiso M-Us / Admin |
| `PATCH` | `/usuarios/resetear` | Resetear usuario | Admin / Z-Us |
| `DELETE` | `/usuarios/{cuit}` | Eliminar usuario | Permiso R-Us / Admin |

### 📦 Productos (`/api/productos`)

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `GET` | `/productos/{id}` | Obtener producto por ID | — |
| `GET` | `/productos/enlistar` | Listar productos (paginado) | PF / Z-Pr |
| `GET` | `/productos/categorias/{nombre}` | Productos por categoría | PF / Z-Pr / Distribuidoras |
| `POST` | `/productos` | Crear producto | — |
| `PATCH` | `/productos/{id}` | Actualizar producto | — |
| `DELETE` | `/productos/{id}` | Eliminar producto | — |

### 🏷️ Categorías (`/api/categorias`)

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `GET` | `/categorias` | Listar categorías | Z-Pr / A-Di / P-X |
| `GET` | `/categorias/por-distribuidora` | Categorías por distribuidora | Z-Pr / Distribuidoras |

### 🏭 Marcas (`/api/marcas`)

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `GET` | `/marcas/categorias/{nombre}` | Marcas por categoría | PF / Z-Pr / Distribuidoras |

### 🏢 Distribuidoras (`/api/distribuidoras`)

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `GET` | `/distribuidoras/{cuit}` | Obtener distribuidora por CUIT | Empleado / Admin |
| `POST` | `/distribuidoras` | Crear distribuidora | A-Di / Admin |
| `PATCH` | `/distribuidoras` | Modificar distribuidora | M-Di / Admin |
| `DELETE` | `/distribuidoras/{cuit}` | Eliminar distribuidora | R-Di / Admin |

### 🧑 Personas Físicas (`/api/personas-fisicas`)

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `GET` | `/personas-fisicas/{cuit}` | Obtener persona física por CUIT | Admin / Empleado |
| `POST` | `/personas-fisicas` | Crear persona física | A-Pf / Admin |
| `PATCH` | `/personas-fisicas` | Modificar persona física | M-Pf / Admin |
| `DELETE` | `/personas-fisicas/{cuit}` | Eliminar persona física | R-Pf / Admin |

### 📋 Presupuestos (`/api/presupuestos`)

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `GET` | `/presupuestos/{id}` | Obtener presupuesto por ID | — |
| `GET` | `/presupuestos/enlistar` | Listar presupuestos (paginado) | Y-Pr / Admin |
| `GET` | `/presupuestos/detalles/**` | Detalles de presupuesto | PF / Distribuidoras / Y-Pr |
| `GET` | `/presupuestos/verificarFechaLimite/{id}` | Verificar fecha límite | PF / Y-Pr / Admin |
| `POST` | `/presupuestos` | Crear presupuesto | Persona Física |
| `POST` | `/presupuestos/rechazar/{id}` | Rechazar presupuesto | Distribuidoras |

### 💰 Cotizaciones (`/api/cotizaciones`)

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `GET` | `/cotizaciones/{id}` | Obtener cotización por ID | — |
| `GET` | `/cotizaciones/enlistar/{idPresupuesto}` | Listar cotizaciones de un presupuesto | PF / Y-Pr / Admin |
| `GET` | `/cotizaciones/mensajes-rechazo` | Obtener mensajes de rechazo | Persona Física |
| `POST` | `/cotizaciones/{idPresupuesto}` | Crear cotización | Distribuidoras |
| `POST` | `/cotizaciones/aceptar/{idCotizacion}` | Aceptar cotización | Persona Física |
| `POST` | `/cotizaciones/rechazar/{idCotizacion}` | Rechazar cotización | Persona Física |

### 📊 Reportes (`/api/reporte`)

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `POST` | `/reporte/productos-solicitados` | Productos más solicitados (general) | Distribuidoras / P-X |
| `POST` | `/reporte/productos-solicitados/producto` | Reporte por producto específico | Distribuidoras / P-X |
| `POST` | `/reporte/productos-solicitados/marca` | Reporte por marca específica | Distribuidoras / P-X |
| `POST` | `/reporte/cotizaciones-ganadas-perdidas` | Cotizaciones ganadas/perdidas | Distribuidoras / C-X |
| `POST` | `/reporte/cotizaciones-rechazadas-mensajes` | Mensajes de cotizaciones rechazadas | Distribuidoras / C-X |
| `POST` | `/reporte/cotizaciones-rechazadas-mensajes-general` | Mensajes de rechazo (general) | Distribuidoras / C-X |
| `GET` | `/reporte/trazabilidad-login-logout/{cuit}` | Trazabilidad de login/logout | Admin / T-X |
| `GET` | `/reporte/trazabilidad-persona-fisica/{cuit}` | Trazabilidad de persona física | Admin / A-X |

### 👥 Grupos y Permisos

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `GET` | `/grupos` | Listar grupos | A-Gr / M-Gr / R-Gr / Admin |
| `GET` | `/grupos/enlistar` | Enlistar todos los grupos | M-Gr / Admin |
| `POST` | `/grupos` | Crear grupo | A-Gr / Admin |
| `PATCH` | `/grupos` | Modificar grupo | M-Gr / Admin |
| `DELETE` | `/grupos/{id}` | Eliminar grupo | R-Gr / Admin |
| `GET` | `/permisos/enlistar` | Listar permisos | M-Gr / Admin |

---

## 🔒 Seguridad y Autenticación

- **JWT (JSON Web Tokens)** almacenado en cookies HTTP-Only (`accessToken`) con política `SameSite=Strict`.
- **Refresh Token** en cookie separada con expiración de 48hs.
- **OAuth2 Resource Server** para validación de tokens.
- **BCrypt** (factor 10) para hashing de contraseñas.
- **Sesiones Stateless** — no se almacena estado en el servidor.
- **CORS** configurado para `http://localhost:4200` (frontend Angular).
- **Filtro JWT personalizado** (`JwtFromCookieFilter`) que extrae el token de las cookies.
- **Verificación por código** para restauración de contraseñas con expiración automática (tarea programada).

---

## 🎭 Roles y Permisos

El sistema maneja los siguientes roles principales:

| Rol | Descripción |
|---|---|
| `ADMIN` | Administrador con acceso completo |
| `EMPLEADO` | Empleado de la empresa |
| `PERSONA FISICA` | Cliente persona física |
| `DISTRIBUIDORAS` | Empresa distribuidora |

Además, se utilizan **permisos granulares** asignados a grupos:

| Permiso | Descripción |
|---|---|
| `A-Us`, `M-Us`, `R-Us`, `Z-Us` | Alta, Modificación, Baja y Reset de Usuarios |
| `A-Di`, `M-Di`, `R-Di` | Alta, Modificación y Baja de Distribuidoras |
| `A-Pf`, `M-Pf`, `R-Pf` | Alta, Modificación y Baja de Personas Físicas |
| `A-Gr`, `M-Gr`, `R-Gr` | Alta, Modificación y Baja de Grupos |
| `Z-Pr`, `Y-Pr` | Gestión de Productos / Presupuestos |
| `P-X` | Reportes de productos solicitados |
| `C-X` | Reportes de cotizaciones |
| `T-X` | Trazabilidad de login/logout |
| `A-X` | Trazabilidad de personas físicas |

---

## 🧪 Tests

Ejecutar los tests con:

```bash
.\mvnw.cmd test
```

Los tests se encuentran en `src/test/java/com/uai/BuildLand/`.

---

## 📄 Licencia

Proyecto académico desarrollado para la **Universidad Abierta Interamericana (UAI)**.

