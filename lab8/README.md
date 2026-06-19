# lab8

Prosty projekt JHipster w `lab8`, zawierający aplikację Angular + Spring Boot oraz model danych zdefiniowany w pliku `database.jdl`.

## Opis projektu

Aplikacja obsługuje wypożyczalnię samochodów z trzema głównymi encjami:

- `Car` — samochody dostępne do wynajmu
- `Driver` — kierowcy przypisani do samochodów
- `RentalOffice` — wypożyczalnie w miastach

Model danych jest opisany w `database.jdl`, a JHipster generuje na jego podstawie kod backendu i frontendowe komponenty.

## Struktura bazy danych

### Encje

- `Car`
  - `brand: String` (wymagane)
  - `model: String` (wymagane)
  - `pricePerHour: Float` (wymagane)

- `Driver`
  - `firstName: String` (wymagane)
  - `lastName: String` (wymagane)
  - `licenseDate: LocalDate`

- `RentalOffice`
  - `officeName: String` (wymagane)
  - `city: String` (wymagane)
  - `status: OfficeStatus`

### Enum

- `OfficeStatus`
  - `OPEN`
  - `CLOSED`
  - `MAINTENANCE`

### Relacje

- `Driver` ma relację `OneToOne` do `Car` jako `currentCar`
- `RentalOffice` ma relację `OneToMany` do `Car` jako `car`
- `Driver` ma relację `ManyToMany` do `RentalOffice` jako `rentalOffice`

To oznacza, że:

- jeden kierowca może mieć przypisany dokładnie jeden samochód,
- jedna wypożyczalnia może mieć wiele samochodów,
- Kierowcy mogą być powiązani z wieloma wypożyczalniami.

## Uruchamianie aplikacji

1. Zainstaluj zależności frontendowe:

```bash
./npmw install
```

2. Uruchom backend i frontend w dwóch terminalach:

```bash
./npmw run backend:start
./npmw run start
```

3. Otwórz aplikację w przeglądarce:

```text
http://localhost:8080
```

## Budowanie

Aby uruchomić testy i zbudować aplikację:

```bash
./mvnw verify
```

Aby zbudować wersję produkcyjną:

```bash
./mvnw -Pprod clean verify
```

## Najważniejsze pliki

- `database.jdl` — model encji i relacji dla aplikacji
- `.yo-rc.json` — konfiguracja JHipster
- `src/main/java` — kod serwera Spring Boot
- `src/main/webapp` — kod klienta Angular
- `pom.xml` — konfiguracja Maven
- `package.json` — skrypty i zależności Node

## Uwaga

Jeśli chcesz zmodyfikować model danych, zaktualizuj `database.jdl` i wygeneruj ponownie encje JHipster.

### PWA Support

JHipster ships with PWA (Progressive Web App) support, and it's turned off by default. One of the main components of a PWA is a service worker.

The service worker initialization code is disabled by default. To enable it, uncomment the following code in `src/main/webapp/app/app.config.ts`:

```typescript
ServiceWorkerModule.register('ngsw-worker.js', { enabled: false }),
```

### Managing dependencies

For example, to add [Leaflet](https://leafletjs.com/) library as a runtime dependency of your application, you would run the following command:

```bash
./npmw install --save --save-exact leaflet
```

To benefit from TypeScript type definitions from [DefinitelyTyped](https://definitelytyped.org/) repository in development, you would run the following command:

```bash
./npmw install --save-dev --save-exact @types/leaflet
```

Then you would import the JS and CSS files specified in library's installation instructions so that [esbuild][] knows about them:
Edit [src/main/webapp/app/app.config.ts](src/main/webapp/app/app.config.ts) file:

```typescript
import 'leaflet/dist/leaflet.js';
```

Edit [src/main/webapp/content/scss/vendor.scss](src/main/webapp/content/scss/vendor.scss) file:

```typescript
@import 'leaflet/dist/leaflet.css';
```

Note: There are still a few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development](https://www.jhipster.tech/development/).

### Using Angular CLI

You can also use [Angular CLI](https://angular.dev/tools/cli) to generate some custom client code.

For example, the following command:

```bash
ng generate component my-component
```

will generate few files:

```bash
create src/main/webapp/app/my-component/my-component.html
create src/main/webapp/app/my-component/my-component.ts
update src/main/webapp/app/app.config.ts
```

## Building for production

### Packaging as jar

To build the final jar and optimize the lab8 application for production, run:

```bash
./mvnw -Pprod clean verify
```

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

```bash
java -jar target/*.jar
```

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

```bash
./mvnw -Pprod,war clean verify
```

### JHipster Control Center

JHipster Control Center can help you manage and control your application(s). You can start a local control center server (accessible on http://localhost:7419) with:

```bash
docker compose -f src/main/docker/jhipster-control-center.yml up
```

## Testing

### Spring Boot tests

To launch your application's tests, run:

```bash
./mvnw verify
```

### Client tests

Unit tests are run by Vitest. They're located near components and can be run with:

```bash
./npmw test
```

## Others

### Code quality using Sonar

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```bash
docker compose -f src/main/docker/sonar.yml up -d
```

Note: we have turned off forced authentication redirect for UI in [src/main/docker/sonar.yml](src/main/docker/sonar.yml) for out of the box experience while trying out SonarQube, for real use cases turn it back on.

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```bash
./mvnw -Pprod clean verify sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```bash
./mvnw initialize sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

Additionally, Instead of passing `sonar.password` and `sonar.login` as CLI arguments, these parameters can be configured from [sonar-project.properties](sonar-project.properties) as shown below:

```bash
sonar.login=admin
sonar.password=admin
```

For more information, refer to the [Code quality page][].

### Docker Compose support

JHipster generates a number of Docker Compose configuration files in the [src/main/docker/](src/main/docker/) folder to launch required third party services.

For example, to start required services in Docker containers, run:

```bash
docker compose -f src/main/docker/services.yml up -d
```

To stop and remove the containers, run:

```bash
docker compose -f src/main/docker/services.yml down
```

[Spring Docker Compose Integration](https://docs.spring.io/spring-boot/reference/features/dev-services.html) is enabled by default. It's possible to disable it in `application.yml`:

```yaml
spring:
  ...
  docker:
    compose:
      enabled: false
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a Docker image of your app by running:

```bash
npm run java:docker
```

Or build an arm64 Docker image when using an arm64 processor OS, i.e., Apple Silicon chips (M\*), running:

```bash
npm run java:docker:arm64
```

Then run:

```bash
docker compose -f src/main/docker/app.yml up -d
```

For more information refer to [Docker and Docker-Compose](https://www.jhipster.tech/documentation-archive/v9.1.0/docker-compose/), this page also contains information on the Docker Compose sub-generator (`jhipster docker-compose`), which is able to generate Docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration](https://www.jhipster.tech/documentation-archive/v9.1.0/setting-up-ci/) page for more information.

## References

- [JHipster Homepage and latest documentation](https://www.jhipster.tech/)
- [JHipster 9.1.0 archive](https://www.jhipster.tech/documentation-archive/v9.1.0)
- [Using JHipster in development](https://www.jhipster.tech/documentation-archive/v9.1.0/development/)
- [Using Docker and Docker-Compose](https://www.jhipster.tech/documentation-archive/v9.1.0/docker-compose)
- [Using JHipster in production](https://www.jhipster.tech/documentation-archive/v9.1.0/production/)
- [Running tests page](https://www.jhipster.tech/documentation-archive/v9.1.0/running-tests/)
- [Code quality page](https://www.jhipster.tech/documentation-archive/v9.1.0/code-quality/)
- [Setting up Continuous Integration](https://www.jhipster.tech/documentation-archive/v9.1.0/setting-up-ci/)
- [Node.js](https://nodejs.org/)
- [NPM](https://www.npmjs.com/)
- [BrowserSync](https://www.browsersync.io/)
- [Jest](https://jestjs.io)
- [Leaflet](https://leafletjs.com/)
- [DefinitelyTyped](https://definitelytyped.org/)
- [Angular CLI](https://angular.dev/tools/cli)
