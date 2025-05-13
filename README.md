<h1 align="center"><a href="https://github.com/martinezdom/BatBatCar" style="text-decoration: none;">Bat Bat Car</a></h1>

<p align="center">
  Aplicación web de gestión de viajes y reservas desarrollada con Spring Boot.
</p>

<p align="center">
  <a href="#"><img alt="Java" src="https://img.shields.io/badge/Java-11-blue?logo=java"></a>
  <a href="#"><img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-2.6.6-brightgreen?logo=springboot"></a>
  <a href="#"><img alt="Thymeleaf" src="https://img.shields.io/badge/Thymeleaf-3.0.15-green?logo=thymeleaf"></a>
  <a href="#"><img alt="Maven" src="https://img.shields.io/badge/Maven-red?logo=apachemaven"></a>
  <a href="#"><img alt="MySQL" src="https://img.shields.io/badge/MySQL-8.0.29-blue?logo=mysql"></a>
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/License-MIT-yellow.svg"></a>
</p>

---

## Descripción
**Bat Bat Car** es una aplicación web que permite a los usuarios consultar, reservar y cancelar viajes. El proyecto está desarrollado con **Spring Boot**, empleando el patrón **MVC** y utilizando **Thymeleaf** como motor de plantillas.

## Características
- Consulta de viajes: Ver rutas, precio, plazas, duración y hora de salida.
- Reserva de viajes: Selección de número de plazas y reserva online.
- Cancelación de reservas.
- Validación de formularios.
- Interfaz moderna y dinámica.

## Tecnologías
- **Java 11**
- **Spring Boot 2.6.6**
- **Thymeleaf 3.0.15**
- **Maven**
- **MySQL 8.0.29**
- **H2 Database** (por defecto) / **MySQL** (opcional)

## Instalación
```bash
# 1. Clonar el repositorio
git clone https://github.com/martinezdom/BatBatCar.git
cd BatBatCar
```
```bash
# 2. Compilar el proyecto
./mvnw clean install
```
```bash
# 3. Ejecutar la aplicación
./mvnw spring-boot:run
```

## Estructura
<pre>
BatBatCar/
├── src/
│   ├── main/
│   │   ├── java/                     # Lógica de negocio
│   │   └── resources/
│   │       ├── templates/            # Vistas Thymeleaf
│   │       ├── static/               # CSS, JS, imágenes
│   │       └── application.properties
│   └── test/                         # Tests
├── pom.xml                           # Configuración Maven
</pre>

## Autor
Miguel Ángel Martínez Domínguez  
[@martinezdom](https://github.com/martinezdom)
