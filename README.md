# Temperature Converter - JavaFX, Database, Jenkins and Docker

Introduction to GUI, Images, and the Database  
AD / Sprint 4 / Fall 2026

## 1. Assignment Description

The task was to convert the earlier command-line Temperature Converter into a JavaFX application that stores its data in a database, and to set up a complete build and delivery pipeline around it.

Key requirements and deliverables:

- A JavaFX graphical user interface for converting temperatures.
- A database with at least two related tables. The interface must interact with the database.
- Unit tests for the classes, and JaCoCo for code coverage.
- A `Jenkinsfile` that builds, tests and publishes the project.
- A `Dockerfile`. The image is run locally and through Jenkins, and deployed to Docker Hub.
- The application is run both locally and as a Docker image, displayed through an X server (XMing).

Submission links:

- GitHub repository: https://github.com/XucyXi/Tuntiharjoitukset_OTP1
- Docker Hub image: https://hub.docker.com/r/jerevla/temperature-converter

## 2. Technologies & Tools Used

| Area | Tool |
|---|---|
| Language / runtime | Java 21 |
| GUI | JavaFX 21.0.4 |
| Database | H2 2.2.224 (embedded, file-based) |
| Build | Maven 3 (maven-shade-plugin for the runnable jar) |
| Testing | JUnit 5.10.2 |
| Code coverage | JaCoCo 0.8.11 |
| CI/CD | Jenkins (declarative pipeline) |
| Containers | Docker Desktop, Docker Hub |
| X server | XMing (shows the container's GUI on Windows) |
| IDE | IntelliJ IDEA |

## 3. Design Approach & Implementation Method

### Structure

The code is in the package `assignments.inclass1` and is split into small classes with one responsibility each:

| Class | Responsibility |
|---|---|
| `Main` | JavaFX user interface and event handling |
| `Launcher` | Plain entry point that starts `Main`. It is needed so the fat jar runs without "JavaFX runtime components are missing" |
| `TemperatureConverter` | Conversion logic (Celsius, Fahrenheit, Kelvin) |
| `DBConnection` | Opens the H2 connection and creates the tables and seed data |
| `TemperatureUnitDAO` | Reads units from the database |
| `TempRecordDAO` | Saves and reads conversion history |
| `TemperatureUnit`, `TempRecord` | Data classes (Java records) |

### GUI

The window has a text field for the value, two drop-down lists for the source and target units, and a **Muunna** (Convert) button. The result is shown under the controls, and a table lists the conversion history (from, to, input, result, time). Both a comma and a dot are accepted as the decimal separator, and invalid input shows an error message instead of crashing.

### Database design

There are two related tables:

- `temperature_unit` (`id`, `name`, `symbol`) holds the units Celsius, Fahrenheit and Kelvin. They are inserted automatically on startup.
- `temp_record` (`id`, `from_unit_id`, `to_unit_id`, `input_value`, `result_value`, `created_at`) holds one row per conversion. `from_unit_id` and `to_unit_id` are foreign keys to `temperature_unit.id`.

The history table in the GUI is loaded with a `JOIN` that links `temp_record` to `temperature_unit` twice, once for the source unit and once for the target unit.

### Key decisions

- **H2 embedded database:** it needs no separate server, so the app and the Docker image run on their own. Tests use an in-memory H2 database, so they do not touch real data.
- **All conversions go through Celsius:** `convert(from, to, value)` converts to Celsius first and then to the target unit, so supporting a new unit needs only two new methods.
- **Two-stage Dockerfile:** the first stage builds the jar on Linux with Maven, and the second stage is a slim Java 21 runtime with the GTK libraries JavaFX needs. A jar built on Windows would contain Windows-only JavaFX libraries and would not run in the container.
- **Jenkins pipeline stages:** Checkout, Build & Test (JUnit and JaCoCo reports), Build Docker Image, Push to Docker Hub. The Docker Hub login uses Jenkins credentials, so no password is stored in the repository.

## 4. Testing & Quality Assurance Steps

### Automated tests (JUnit 5)

| Test class | What it verifies |
|---|---|
| `TemperatureConverterTest` | All conversions (F→C, C→F, K→C, C→K), the extreme-temperature check including the edge values -40 and 50, conversion between all unit pairs, and an exception for an unknown unit |
| `DBConnectionTest` | The connection opens, both tables are created, and the three units are seeded |
| `TemperatureUnitDAOTest` | `findAll` returns three units, `findByName` finds an existing unit and returns empty for an unknown one, `toString` formatting |
| `TempRecordDAOTest` | `insert` returns an id, `findAll` returns the joined unit data, newest record comes first, `deleteAll` empties the table |

Every database test gets its own empty in-memory database, so the tests are independent of each other.

Run all tests with `mvn clean test`. The JaCoCo coverage report is generated at `target/site/jacoco/index.html`. The JavaFX classes `Main` and `Launcher` are excluded from coverage, because they only contain user-interface code.

### Continuous integration

The Jenkins pipeline runs the tests on every build and publishes the JUnit and JaCoCo results. The build only continues to the Docker steps if the tests pass.

### Manual tests

| Scenario | Expected result | Result |
|---|---|---|
| 100 °C → °F | 212.00 °F | Passed |
| Convert with a decimal comma (e.g. `36,6`) | The value is accepted | Passed |
| Empty or non-numeric input | "Syötä kelvollinen luku." message, no crash | Passed |
| Several conversions, then restart the app | History is still shown after restart | Passed |
| Run as a Docker image through XMing | The window opens and works the same as locally | Passed |

## 5. How to Run

### Prerequisites

- JDK 21 and Maven (IntelliJ includes Maven)
- Docker Desktop (for the Docker run)
- XMing, or another X server, started with **Multiple windows**, **Start no client** and **No Access Control** enabled

### Run locally

```
mvn javafx:run
```

In IntelliJ you can also run the `Launcher` class. Running `Main` directly gives the error "JavaFX runtime components are missing".

### Run the tests

```
mvn clean test
```

### Build and run the Docker image

```
docker build -t jerevla/temperature-converter:latest .
docker run --rm -e DISPLAY=host.docker.internal:0.0 jerevla/temperature-converter:latest
```

XMing must be running before the `docker run` command, otherwise the window cannot be displayed.

### Run the published image from Docker Hub

```
docker pull jerevla/temperature-converter:latest
docker run --rm -e DISPLAY=host.docker.internal:0.0 jerevla/temperature-converter:latest
```

### Jenkins

The pipeline is defined in the `Jenkinsfile`. It requires a Maven installation named `DefaultMaven`, Docker available on the agent, and a Docker Hub credential in Jenkins with the ID `docker-hub-password`.



## Linkit
[In-class 2 JoCoCo](https://users.metropolia.fi/~jerevla/OTP-1-inclass/inclass2/)



*Note: AI assistance was utilized for proofreading and formatting this documentation.*
