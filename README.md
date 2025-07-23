# API Spring Boot WebFlux

Proyecto de ejemplo para una API reactiva usando **Spring Boot WebFlux** y **MongoDB**. Permite gestionar productos y categorías, con validación de formularios y carga de imágenes.

## Características

- API REST reactiva con Spring WebFlux
- Persistencia en MongoDB
- Gestión de productos y categorías
- Validación de formularios con Thymeleaf y Bean Validation
- Carga y visualización de imágenes de productos
- Datos de prueba generados automáticamente al iniciar el proyecto

## Requisitos

- Java 17+
- Maven 3.8+
- MongoDB en ejecución (local o remoto)

## Instalación

1. Clona el repositorio:

git clone https://github.com/wildsrincon/api-spingboot-webflux.git 
cd api-spingboot-webflux

2. Instala las dependencias:

mvn clean install

## Ejecución

1. Inicia MongoDB en tu máquina local.
2. Ejecuta la aplicación:

mvn spring-boot:run

3. Accede a la interfaz web en [http://localhost:8080](http://localhost:8080).

## Endpoints principales

- `/products` - Listado de productos
- `/form` - Formulario para crear/editar productos
- `/uploads/img/{image}` - Visualización de imágenes de productos

## Estructura del proyecto

- `src/main/java/org/wildsrincon/webflux/app/model/documents` - Modelos de datos (`Product`, `Category`)
- `src/main/java/org/wildsrincon/webflux/app/controller` - Controladores REST y web
- `src/main/java/org/wildsrincon/webflux/app/service` - Servicios de negocio
- `src/main/resources/templates` - Vistas Thymeleaf
- `src/main/resources/static/uploads/img` - Imágenes de productos

## Validación de formularios

Se utiliza Bean Validation (`@NotEmpty`, `@NotNull`, `@Positive`) y mensajes de error en las vistas Thymeleaf.

## Datos de prueba

Las categorías y productos de ejemplo se crean automáticamente solo si la base de datos está vacía al iniciar la aplicación.

## Autor

wildsrincon

---

¡Contribuciones y sugerencias son bienvenidas!
