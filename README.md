# Franchise API - Gestión Reactiva de Franquicias

API REST reactiva desarrollada con **Spring Boot 4.1.2 (WebFlux)**, **MongoDB Reactivo**, **Docker**, **Terraform (MongoDB Atlas sobre AWS)** y desplegada en la nube con **Render**, siguiendo los principios de **Arquitectura Limpia (Clean / Hexagonal Architecture)** y **Programación Reactiva No Bloqueante**.

---

## 🌐 Despliegue en la Nube (Listo para Probar)

* **URL Pública de la API:** `https://franchise-api-supf.onrender.com`
* **Health Check:** [https://franchise-api-supf.onrender.com/actuator/health](https://franchise-api-supf.onrender.com/actuator/health)
* **Base de Datos en la Nube:** Clúster **MongoDB Atlas (M0 en AWS `US_EAST_1`)** aprovisionado mediante **Terraform**.

> ℹ️ **Nota sobre el plan gratuito de Render:** si la API lleva más de 15 minutos sin recibir tráfico entra en reposo automático. La **primera petición** tras estar dormida puede tardar alrededor de **50 segundos** mientras despierta el contenedor; las siguientes peticiones responden con normalidad.

---

## 🚀 Stack Tecnológico

* **Java 21**: Uso de `records` inmutables y características modernas del lenguaje.
* **Spring Boot 4.1.2 (Spring WebFlux)**: Servidor reactivo basado en Netty (I/O no bloqueante con `Mono` y `Flux`).
* **Spring Data MongoDB Reactive**: Persistencia reactiva de documentos en MongoDB.
* **Docker & Docker Compose**: Empaquetado multi-stage de la aplicación y orquestación local junto con MongoDB 7.0.
* **Terraform (IaC)**: Aprovisionamiento automatizado de MongoDB Atlas sobre AWS (`mongodb/mongodbatlas`).
* **Render**: Despliegue continuo en la nube a partir del `Dockerfile`.
* **Testing**: JUnit 5, Mockito y Project Reactor `StepVerifier`.

---

## 🏗️ Arquitectura del Proyecto

El código fuente sigue una estructura hexagonal separada en capas:

```text
src/main/java/dev/johnbernal/franchise/
├── domain/                    # Capa central independiente de frameworks
│   ├── exception/             # Excepciones de reglas de negocio
│   ├── model/                 # Modelos de dominio inmutables (Java records)
│   └── port/                  # Contratos de entrada y salida
│       ├── in/                # FranquiciaUseCasePort
│       └── out/               # FranquiciaRepositoryPort
├── application/               # Capa de aplicación (orquestación reactiva)
│   └── service/               # FranquiciaServiceImpl
└── infrastructure/            # Adaptadores externos y configuración
    ├── adapter/
    │   ├── in/web/            # Controladores REST, DTOs y manejo global de errores
    │   └── out/persistence/   # Adaptador, documentos, mapper y repositorio MongoDB
    └── config/                # Configuración de beans de dominio
```

---

## 🔌 Endpoints Disponibles

Puedes consumir cualquiera de los siguientes endpoints cambiando únicamente la **Base URL** según el entorno que quieras probar:

* **Nube (Render + MongoDB Atlas AWS):** `https://franchise-api-supf.onrender.com`
* **Local (Docker / IDE):** `http://localhost:8080`

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/v1/franquicias` | Crear una nueva franquicia |
| `GET` | `/api/v1/franquicias` | Listar todas las franquicias registradas |
| `GET` | `/api/v1/franquicias/{franquiciaId}` | Consultar una franquicia por su ID |
| `PATCH` | `/api/v1/franquicias/{franquiciaId}/nombre` | Actualizar el nombre de una franquicia |
| `POST` | `/api/v1/franquicias/{franquiciaId}/sucursales` | Agregar una nueva sucursal a una franquicia |
| `PATCH` | `/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/nombre` | Actualizar el nombre de una sucursal |
| `POST` | `/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos` | Agregar un nuevo producto a una sucursal |
| `PATCH` | `/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/nombre` | Actualizar el nombre de un producto |
| `DELETE` | `/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}` | Eliminar un producto de una sucursal |
| `PATCH` | `/api/v1/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock` | Modificar el stock de un producto |
| `GET` | `/api/v1/franquicias/{franquiciaId}/productos/mayor-stock` | Obtener el producto con mayor stock por cada sucursal de la franquicia |
| `GET` | `/actuator/health` | Verificar el estado de salud de la aplicación y conexión con MongoDB |

---

## 🐳 Puesta en Marcha Local

Asegúrate de tener **Docker Desktop** encendido antes de iniciar. Al correr en local sin variables externas, la aplicación utiliza automáticamente el MongoDB local definido en `docker-compose.yml` (`franchise_db`).

### Opción A: Levantar API + MongoDB con Docker Compose (Recomendado)

Este modo construye la imagen de la aplicación usando el `Dockerfile` multi-stage y levanta dos contenedores (`franchise-api` y `franchise-mongodb`) conectados automáticamente en red interna:

> ⚠️ Si tenías la aplicación corriendo desde tu IDE (Eclipse / IntelliJ) en el puerto `8080`, detenla antes para evitar conflicto de puertos.

```bash
docker compose up -d --build
```

Comandos útiles:

```bash
# Ver logs de la API en tiempo real
docker compose logs -f api

# Detener únicamente la API (si quieres volver a ejecutarla desde el IDE contra MongoDB en Docker)
docker compose stop api

# Detener todos los contenedores
docker compose down
```

### Opción B: MongoDB en Docker + API desde Código Local

Si prefieres ejecutar la aplicación directamente desde Maven o tu IDE:

1. Levantar únicamente el contenedor de MongoDB:

```bash
docker compose up -d mongodb
```

2. Ejecutar la aplicación Spring Boot:

* **Windows (PowerShell / CMD):**
  ```powershell
  .\mvnw spring-boot:run
  ```
* **Linux / macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```

### Conexión desde MongoDB Compass (Opcional)

Con el contenedor `franchise-mongodb` encendido, puedes inspeccionar la base de datos local conectándote a:

```text
mongodb://localhost:27017
```

* Base de datos: `franchise_db`
* Colección: `franquicias`

---

## ☁️ Infraestructura como Código (Terraform + MongoDB Atlas en AWS)

La carpeta [`terraform/`](terraform/) contiene la definición de infraestructura para aprovisionar automáticamente la base de datos en **MongoDB Atlas sobre AWS (`US_EAST_1`)** sin crear recursos manualmente en consola:

* Proyecto en MongoDB Atlas (`mongodbatlas_project`)
* Clúster gratuito `M0` sobre AWS (`mongodbatlas_advanced_cluster`)
* Usuario de base de datos con permiso `readWrite` sobre `franchise_db` (`mongodbatlas_database_user`)
* Regla de acceso de red (`mongodbatlas_project_ip_access_list`)

> 🔒 **Seguridad:** Por buenas prácticas, el repositorio **no** expone llaves privadas ni contraseñas. El archivo `terraform.tfvars`, los binarios `.terraform/` y los archivos `*.tfstate*` están excluidos en `.gitignore`. En el repo solo se incluye la plantilla [`terraform/terraform.tfvars.example`](terraform/terraform.tfvars.example).

### Pasos para aprovisionar con tus propias credenciales de Atlas

1. Copia [`terraform/terraform.tfvars.example`](terraform/terraform.tfvars.example) como `terraform/terraform.tfvars` y completa tu `atlas_org_id`, tus credenciales de Atlas (**Service Account** o **API Key**) y `db_password`.
2. Inicializa, valida el plan y aplica la infraestructura:

```powershell
cd terraform
terraform init
terraform plan
terraform apply
```

### Conectar tu contenedor Docker local contra Atlas usando el output de Terraform

Una vez creado el clúster, Terraform guarda de forma local la URI completa (con usuario, contraseña y base de datos) en un output sensible llamado `mongodb_connection_uri`.

Puedes guardarla en una variable de entorno temporal de tu terminal sin exponer la contraseña en código y pasársela al contenedor Docker:

* **Windows (PowerShell):**
  ```powershell
  cd terraform
  $env:ATLAS_URI = terraform output -raw mongodb_connection_uri
  cd ..
  docker build -t franchise-api:1.0 .
  docker run --rm -p 8080:8080 -e SPRING_MONGODB_URI="$env:ATLAS_URI" franchise-api:1.0
  ```

* **Linux / macOS:**
  ```bash
  cd terraform
  export ATLAS_URI=$(terraform output -raw mongodb_connection_uri)
  cd ..
  docker build -t franchise-api:1.0 .
  docker run --rm -p 8080:8080 -e SPRING_MONGODB_URI="$ATLAS_URI" franchise-api:1.0
  ```

*(Cuando ya no necesites la infraestructura en Atlas, puedes eliminarla por completo ejecutando `terraform destroy` dentro de `terraform/`).*

---

## 🧪 Ejecución de Pruebas Unitarias

Las pruebas unitarias cubren los casos de uso y validaciones de negocio de `FranquiciaServiceImpl` utilizando Mockito y `StepVerifier`:

* **Windows:**
  ```powershell
  .\mvnw test
  ```
* **Linux / macOS:**
  ```bash
  ./mvnw test
  ```

*(También puedes ejecutar `FranquiciaServiceImplTest` directamente desde tu IDE como JUnit Test).*

---

## 📬 Colección de Pruebas (Postman)

Para probar todos los endpoints fácilmente, el proyecto incluye la colección preconfigurada **`Franchise API - Collection`**:

* **Archivo en el repositorio:** [`postman_collection.json`](postman_collection.json) (importar en Postman mediante **Import → File**).
* **Documentación pública en Postman:** [Ver documentación e importar en Postman](https://documenter.getpostman.com/view/17710398/2sBYHaPwsD) (también permite importar la colección directamente desde **Run in Postman**).

### Orden sugerido de ejecución y guardado automático de variables

La colección cuenta con scripts en sus peticiones de creación que guardan automáticamente los IDs generados en las **variables de colección** para poder ejecutar el resto de endpoints sin copiar IDs manualmente:

1. **`1.1 Crear Franquicia (Exitoso - 201)`** → guarda automáticamente `franquiciaId`.
2. **`2.1 Agregar Sucursal (Exitoso - 201)`** → usa `{{franquiciaId}}` y guarda automáticamente `sucursalId`.
3. **`3.1 Agregar Producto (Exitoso - 201)`** → usa `{{franquiciaId}}` y `{{sucursalId}}`, y guarda automáticamente `productoId`.
4. A partir de ahí puedes ejecutar en cualquier orden las consultas, actualizaciones de nombre/stock, reporte de mayor stock y eliminación de producto.

### Cómo cambiar la URL (`baseUrl`) o editar las variables en Postman

La variable `baseUrl` viene configurada por defecto para entorno local, pero puedes cambiarla fácilmente para probar la aplicación en la nube o cualquier otro ID existente:

* **Para pruebas locales:** `http://localhost:8080`
* **Para pruebas en la nube (Render):** `https://franchise-api-supf.onrender.com`

**Pasos en Postman:**

1. En el panel izquierdo (**Collections**), haz clic sobre la carpeta raíz **`Franchise API - Collection`**.
2. En el panel derecho, selecciona la pestaña **`Variables`**.
3. Verás las siguientes variables disponibles:
   * `baseUrl`: URL base de la API (`http://localhost:8080` o `https://franchise-api-supf.onrender.com`, sin barra `/` al final).
   * `franquiciaId`: ID de la franquicia activa en las pruebas.
   * `sucursalId`: ID de la sucursal activa en las pruebas.
   * `productoId`: ID del producto activo en las pruebas.
4. Haz clic en la columna **`Value`** de la variable que quieras modificar, escribe el nuevo valor y guarda los cambios con **`Ctrl + S`** (Windows/Linux) o **`Cmd + S`** (Mac).
