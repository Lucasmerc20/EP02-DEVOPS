# pagos-svc

Microservicio de pagos construido con Spring Boot 3.3, Java 21 y Docker.
Es el proyecto base de la asignatura Ingeniería DevOps (DOY0101) y la semilla del pipeline que trabajaremos en el semestre.

## Requisitos

- Docker (Desktop en Windows / Engine en Linux)
- Git

## Puesta en marcha

```bash
docker compose up --build -d
```

Se levantan 2 réplicas, una en el puerto 8080 y otra en el 8081.

- Página de presentación: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Consola H2: http://localhost:8080/h2-console
- API REST: http://localhost:8080/api/pagos
- Estado del servicio: http://localhost:8080/actuator/health

## Pruebas locales

```bash
mvn test    # unit tests
mvn verify  # verifica cobertura JaCoCo (exige 100 %)
```

## Modelo de ramificación

### Opciones que evalué

| Modelo | Cómo funciona | Ideal para |
| --- | --- | --- |
| **GitFlow** | Ramas `main` + `develop` + `feature/` + `hotfix/` | Proyectos con releases planificadas y una rama de integración |
| **GitHub Flow** | Solo `main` + `feature/` + pull request | Despliegue continuo simple y equipos pequeños |
| **Trunk-based** | Una sola rama (`main`/`trunk`) + ramas de vida corta | Entrega continua, tronco inestable no permitido |

### Modelo elegido: GitFlow

Elegí **GitFlow** porque el curso se desarrolla durante todo el semestre y cada entrega (EP01, EP02, EP03) es un hito estable. La rama `develop` me permite integrar las features terminadas sin ensuciar `main`, y la rama `hotfix/` me deja corregir un bug en producción sin interrumpir el trabajo en desarrollo. Además, GitFlow separa claramente el código estable (`main`) del código en integración (`develop`), lo que da trazabilidad frente a lo que piden las rúbricas del curso.

Este repositorio lo desarrollo de forma individual, así que mantengo la disciplina de ramas y pull requests aunque el flujo lo ejecute una sola persona.

### Estructura de ramas del proyecto

| Rama | Origen | Propósito | ¿Se borra? |
| --- | --- | --- | --- |
| `main` | inicial | Código estable, siempre listo para producción | No |
| `develop` | main | Integración de features terminadas | No |
| `feature/<nombre>` | develop | Un cambio de funcionalidad | Sí, al fusionarse |
| `hotfix/<nombre>` | main | Corrección urgente de un bug en producción | Sí, al fusionarse |

Regla de naming: `feature/descripcion-corta` y `hotfix/descripcion-corta`, todo en minúsculas y separado por guiones (por ejemplo `feature/pagina-presentacion`).

## Convenciones y buenas prácticas

### Convención de commits

Formato: `tipo(alcance): descripcion-corta`. Escrito en **minúsculas y sin tildes**.

| Tipo | Para qué | Ejemplo |
| --- | --- | --- |
| `feat` | Nueva funcionalidad | `feat(ui): agregar pie de pagina` |
| `fix` | Corrección de bug | `fix(home): corregir titulo` |
| `docs` | Documentación | `docs: agregar changelog` |
| `chore` | Tareas / CI | `chore(ci): agregar workflow hola mundo` |

### Naming de ramas

`feature/<feature-name>` y `hotfix/<feature-name>`, en minúsculas y con guiones. Ejemplos: `feature/pagina-presentacion`, `hotfix/titulo-pagina`.

### Flujo de merge

- Las features y hotfix siempre entran por **pull request**, nunca con push directo a `main` o `develop`.
- Se requiere al menos **1 aprobación** antes de fusionar; como este repo es en solitario, la aprueba el propio autor después de revisar la diff.
- Uso merge commit o squash y **borro la rama** luego de fusionar.

### Estrategia de revisión

- El autor abre el PR y revisa la diff antes de fusionar.
- En un equipo se asigna un revisor que comenta, aprueba o pide cambios; nunca se fusiona un PR sin revisar.
- Antes de cada PR: confirmar que `mvn test` pasa y revisar la diff.

> **Nota: repositorio en solitario (EP01).** Este encargo se desarrolló con una sola cuenta de GitHub, así que el rol de autor y revisor lo cumple la misma persona. El flujo completo (crear la rama desde su base, abrir el PR, revisar la diff, aprobar y fusionar) se hizo en cada cambio para validar las 2 features y el hotfix del indicador IE2.

## Pipeline CI/CD (EP2)

El pipeline está en `.github/workflows/pipeline.yml` y corre en cada push y pull request a `main` y `develop`.

```
pruebas ──┐
          ├──> construir ──> desplegar
seguridad ┘
```

| Etapa | Qué hace |
| --- | --- |
| `pruebas` | Ejecuta `mvn verify`. Si falla una prueba o la cobertura baja del 100 %, el pipeline se detiene. El reporte JaCoCo queda como artefacto. |
| `seguridad` | Snyk revisa las dependencias. Si encuentra vulnerabilidades altas o críticas falla. |
| `construir` | Construye la imagen Docker y la publica en GHCR con el SHA del commit como tag. |
| `desplegar` | Levanta la imagen con Docker Compose en el runner y verifica `/actuator/health` en las 2 réplicas. |

`construir` solo corre si pasan `pruebas` y `seguridad`, y `desplegar` solo corre en push (no en PR).

### Contenedor

El `Dockerfile` es multi-stage: una etapa con Maven compila el jar y la final usa `eclipse-temurin:21-jre-alpine`, que es más liviana. Corre con un usuario sin privilegios y tiene `HEALTHCHECK`. El `.dockerignore` deja fuera `target/`, `.git/` y otros archivos que no se necesitan.

### Seguridad

- **Dependabot** (`.github/dependabot.yml`): revisa semanalmente las dependencias de Maven y las actions, y abre PR hacia `develop`.
- **Snyk**: corre en el pipeline con `--severity-threshold=high`.
- **Política de bloqueo**: si Snyk encuentra una vulnerabilidad alta o crítica, la imagen no se construye y no se despliega hasta corregirla.

Para que funcione hay que crear el secret `SNYK_TOKEN` en *Settings → Secrets and variables → Actions*.

### Orquestación y escalabilidad

`docker-compose.yml` define:

- 2 réplicas del servicio (puertos 8080 y 8081).
- Límite de 0.5 CPU y 512 MB de memoria por réplica (reserva de 0.25 CPU y 256 MB).
- Healthcheck cada 30 s contra `/actuator/health`.
- `restart: unless-stopped` para que se reinicie si se cae.

### Trazabilidad y calidad

- Cada imagen se etiqueta con el SHA del commit (`ghcr.io/lucastapia20/pagos-svc:<sha>`), así se sabe exactamente qué código está desplegado.
- El despliegue usa la misma imagen que se construyó, no se vuelve a compilar.
- El job `desplegar` deja en el log el commit, la imagen, la rama y el estado de los contenedores.
- Los cambios entran por ramas `feature/*` y pull request (GitFlow), y cada ejecución queda en la pestaña Actions.
- La calidad se asegura con las pruebas, la cobertura del 100 % y el análisis de Snyk antes de desplegar.

## Declaración de uso de IA

Se usó Claude Code como apoyo para armar el Dockerfile, el pipeline, el docker-compose y este README. Todo fue revisado y probado por el equipo antes de subirlo. Normativa: https://bibliotecas.duoc.cl/ia

## Conclusiones y reflexión personal

debido a falta de tiempo en lo personal se me hizo un trabajo algo complicado.
