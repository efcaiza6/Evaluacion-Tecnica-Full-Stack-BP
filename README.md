# Banco App

Aplicacion web sencilla para la gestion de clientes, cuentas, movimientos y reportes bancarios.

## Tecnologias

- Backend: Spring Boot, JPA, MySQL
- Frontend: Angular
- Pruebas: JUnit y Jest
- Contenedores: Docker

## Estructura

- `fullstack-bp`: backend Spring Boot
- `banco-front`: frontend Angular
- `docker-compose.yml`: contenedores de frontend y backend

## Base de datos

Crear primero la base de datos en MySQL:

```sql
CREATE DATABASE banco_db;
```

Tambien se incluye un script SQL/export de la base de datos para facilitar las pruebas del proyecto:

`database/banco_db.sql`

Ese archivo se puede importar en MySQL antes de ejecutar la aplicacion.

La configuracion actual del backend esta en:

`fullstack-bp/src/main/resources/application.properties`

## Ejecucion local

### Backend

```powershell
cd fullstack-bp
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
.\mvnw.cmd spring-boot:run
```

Se ejecuta en:

`http://localhost:8080`

### Frontend

```powershell
cd banco-front
npm install
npm start
```

Se ejecuta en:

`http://localhost:4200`

## Docker

Para levantar frontend y backend con Docker:

```powershell
docker compose up --build
```

## Endpoints principales

- `GET /clientes`
- `POST /clientes`
- `GET /cuentas`
- `POST /cuentas`
- `GET /movimientos`
- `POST /movimientos`
- `GET /reportes?clienteId=1&fechaDesde=2026-05-01&fechaHasta=2026-05-31&formato=json`

## Funcionalidades

- CRUD de clientes
- CRUD de cuentas
- CRUD de movimientos
- Validaciones de datos
- Control de saldo disponible
- Limite diario de retiro
- Reporte en JSON y PDF en base64

## Pruebas

### Backend

```powershell
cd fullstack-bp
.\mvnw.cmd test
```

### Frontend

```powershell
cd banco-front
npm test
```
