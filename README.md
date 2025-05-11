# Proyecto Bat Bat Car

## Descripción

**Bat Bat Car** es una aplicación web que permite a los usuarios gestionar viajes y reservas de manera sencilla y eficiente. Los usuarios pueden consultar los detalles de los viajes disponibles, realizar reservas, cancelar viajes y gestionar sus reservas.

Este proyecto es parte de mi portfolio y fue desarrollado utilizando **Spring Boot** para el backend, **Thymeleaf** para las vistas y **Java** como lenguaje principal. Además, se emplea una arquitectura MVC para separar las preocupaciones y facilitar el mantenimiento y expansión de la aplicación.

## Características

- **Gestión de viajes**: Los usuarios pueden consultar detalles de viajes como ruta, plazas disponibles, precio, duración y hora de salida.
- **Reserva de viajes**: Los usuarios pueden realizar reservas para un viaje específico, indicando el número de plazas solicitadas.
- **Cancelación de reservas**: Los usuarios pueden cancelar sus reservas existentes.
- **Interfaz de usuario**: La aplicación tiene una interfaz amigable basada en **Thymeleaf** que se comunica con el backend para mostrar la información actualizada en tiempo real.
- **Validación de entradas**: La aplicación valida las entradas del usuario para asegurar que la información ingresada sea correcta antes de ser procesada.

## Tecnologías Utilizadas

- **Backend**: Spring Boot (Java)
- **Frontend**: Thymeleaf
- **Base de datos**: H2 (en memoria) / MySQL (opcional)
- **Dependencias**:
  - Spring Web
  - Thymeleaf

## Instalación

1. Clona el repositorio:

    ```bash
    git clone https://github.com/martinezdom/bat-bat-car.git
    cd bat-bat-car
    ```

2. Compila el proyecto utilizando Maven:

    ```bash
    ./mvnw clean install
    ```

3. Ejecuta la aplicación:

    ```bash
    ./mvnw spring-boot:run
    ```

4. Abre tu navegador y ve a `http://localhost:8080` para ver la aplicación en acción.

## Estructura del Proyecto

- **`src/main/java`**: Contiene el código fuente de la aplicación, incluyendo controladores, servicios y entidades de JPA.
- **`src/main/resources/templates`**: Contiene las vistas de Thymeleaf para mostrar la interfaz de usuario.
- **`src/main/resources/static`**: Contiene los archivos estáticos como CSS, JavaScript y fuentes.
- **`src/main/resources/application.properties`**: Configuración de la aplicación.

