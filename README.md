<div align="center">
<img src="./assets/eventdeck.png" width="250" alt="EventDeck Logo" />

<h2>EventDeck</h2>

<p>Una aplicación de escritorio para gestionar eventos</p>
</div>

---

### Funciones

En la siguiente lista se enumeran las funciones de EventDeck.

- Gestión de eventos.
- Gestión de tickets.
- Tickets canjeables por QR o código único.
- Exportador de tickets en PDF.
- Invitación a evento por email.
- Creación de tickets en lote.
- Resumen de asistencia.
- Almacenamiento local en SQLite.

### Resumen de arquitectura

EventDeck es una aplicación de escritorio hecha con Java Swing. Usa SQLite para persistir datos de forma local y se organiza en controladores, servicios y repositorios.

- **Servicios:** ejecutan la lógica de negocio.
- **Controladores:** son la capa de presentación de la aplicación. Desde aquí el usuario accede a los
  servicios.
- **Repositorios:** persisten los datos de la aplicación.

### Ejecutar

Para ejecutar EventDeck tenés que cumplir los siguientes requisitos y seguir los pasos a continuación.

#### Requisitos previos

- Tener Java 25 o superior.
- Tener IntelliJ IDEA o acceso a un terminal.
- Opcionalmente un servidor SMTP.

Una vez cumplidos esos requisitos podés probar el software de dos formas.

#### Forma 1: IntelliJ IDEA

1. Cloná el repositorio.

```bash
git clone https://github.com/VeguiDev/event-deck.git
```

2. Entrá al proyecto.

```bash
cd event-deck
```

3. Abrí el proyecto con IntelliJ IDEA.
4. Una vez abierto, ejecutalo con `MAYUS + F10`.
5. Listo.

#### Forma 2: terminal

1. Abrí un terminal.
2. Cloná el repositorio.

```bash
git clone https://github.com/VeguiDev/event-deck.git
```

3. Entrá al proyecto.

```bash
cd event-deck
```

4. Compilá el proyecto.

```bash
./gradlew build
```

En Windows:

```bash
.\gradlew.bat build
```

5. Ejecutá la aplicación.

```bash
./gradlew run
```

En Windows:

```bash
.\gradlew.bat run
```

### Configuración SMTP

La configuración SMTP es opcional y solo hace falta si querés enviar invitaciones por email desde la aplicación. Podés cargar los datos del servidor SMTP desde la pantalla de configuración de EventDeck.

---

Desarrollado por [VeguiDev](https://vegui.dev) como trabajo práctico de POO.
