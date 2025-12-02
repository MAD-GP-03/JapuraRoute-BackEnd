# JapuraRoute

Author: nuwan konara

## Overview
A Kotlin + Spring Boot application (Gradle) providing REST APIs, JWT authentication, and OpenAPI/Swagger documentation.

This README collects quick setup/run instructions and practical troubleshooting steps for problems you already encountered (JDBC URL parsing, Swagger runtime errors, circular bean dependencies, ControllerAdviceBean NoSuchMethodError, and common IntelliJ @Autowired warnings).

---

## Tech stack
- Kotlin
- Spring Boot
- Spring Security (JWT)
- Hibernate / JPA
- Gradle (wrapper)
- springdoc-openapi (Swagger)

---

## Requirements
- JDK 17+ (use the project's configured JDK)
- Gradle wrapper (use `./gradlew`)
- Optional: Docker or a hosted Postgres (Neon, Heroku Postgres, etc.) for DB

---

## Quickstart
1. Build:

```bash
./gradlew clean build
```

2. Run (dev):

```bash
./gradlew bootRun
```

3. Run jar:

```bash
java -jar build/libs/JapuraRoute-0.0.1-SNAPSHOT.jar
```

4. Swagger / OpenAPI (if configured with springdoc):
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Swagger UI: http://localhost:8080/swagger-ui/index.html

If Swagger UI cannot load the API docs, open the JSON URL above in the browser to inspect the error message.

---

## Environment and configuration (.env)
- It's good you use a `.env` or externalized configuration. Keep secrets out of source control.
- For Spring Boot you typically reference environment variables inside `application.properties` or `application.yml`, for example:

```properties
spring.datasource.url=${JDBC_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
```

- For Neon-like Postgres connection strings, use the correct JDBC format: `jdbc:postgresql://<host>:<port>/<database>?<params>` and put username/password in the appropriate properties or use the `jdbc:postgresql://host:port/database` form plus `spring.datasource.username` and `spring.datasource.password`.

Example working JDBC properties:

```properties
spring.datasource.url=jdbc:postgresql://pooler.us-east-1.aws.neon.tech:5432/neondb?sslmode=require&channel_binding=require
spring.datasource.username=
spring.datasource.password=
```

Do not embed credentials in the URL; put them into `spring.datasource.username` and `spring.datasource.password` so JDBC URL parsing doesn't fail.

---

## JWT & Security (high level)
- Register and Login endpoints should be public (permitAll).
- JWT validation should occur in a stateless filter that runs before UsernamePasswordAuthenticationFilter.
- Avoid circular injection between the `JwtAuthenticationFilter` and `SecurityConfig` (see Troubleshooting).

---

## Swagger (springdoc) setup notes
1. Add dependency (example):

```kotlin
dependencies {
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
}
```

2. Add `SwaggerConfig` bean that configures a bearer JWT security scheme (if desired).
3. Permit the swagger endpoints in your security config:

```kotlin
.requestMatchers(
  "/v3/api-docs/**",
  "/swagger-ui/**",
  "/swagger-ui.html"
).permitAll()
```

Compatibility note: If you see runtime errors like `NoSuchMethodError: 'void org.springframework.web.method.ControllerAdviceBean.<init>(java.lang.Object)'`, this usually indicates a binary incompatibility between the `springdoc` / `spring-web` / Spring Framework versions. Align `springdoc` to a version compatible with your Spring Boot / Spring Framework version. Common steps to resolve this are in Troubleshooting below.

---

## Troubleshooting (errors you encountered)

### 1) JDBC URL invalid port number / Unable to parse URL
Symptom: logs show `JDBC URL invalid port number: <something>` or `Unable to parse URL jdbc:postgresql://neondb_owner:...@host/db?...`.

Cause: putting the username:password directly into the JDBC URL host section or malformed URL. The Postgres JDBC driver expects the authority to be `host:port` (not `username:password@host`).

Fix:
- Use `spring.datasource.url=jdbc:postgresql://host:port/database?params` and set `spring.datasource.username` and `spring.datasource.password` separately.

Example:
```properties
spring.datasource.url=jdbc:postgresql://ep-solitary-violet-a48cq0r0-pooler.us-east-1.aws.neon.tech:5432/neondb?sslmode=require&channel_binding=require
spring.datasource.username=
spring.datasource.password=
```

If your provider gives a single connection string like `postgres://user:pass@host:port/db`, convert it to the JDBC form.

---

### 2) `NoSuchMethodError: ControllerAdviceBean.<init>(Object)` (springdoc/Swagger runtime 500)
Symptom: Swagger endpoint fails at runtime with `NoSuchMethodError` referencing `ControllerAdviceBean` (shown in server logs). Swagger UI shows `Failed to load API definition` or `/v3/api-docs` returns status 500.

Cause: Class/method binary incompatibility between versions of Spring Framework classes on the classpath and the version expected by `springdoc` (or another library). This is usually a dependency version mismatch.

Fix checklist (recommended order):
1. Identify Spring Boot version in `build.gradle.kts` (or `gradle.properties`).
2. Choose a `springdoc` starter version compatible with your Spring Boot:
   - Spring Boot 3 / Spring Framework 6 -> springdoc 2.x (starter artifact `org.springdoc:springdoc-openapi-starter-webmvc-ui` 2.x)
   - Spring Boot 2.x -> springdoc 1.6.x
3. Update the dependency and refresh Gradle. Example (for modern Spring Boot):

```kotlin
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
```

4. Run `./gradlew clean build --refresh-dependencies`.
5. If the problem persists, run `./gradlew dependencies --configuration runtimeClasspath` and search for conflicting versions of `spring-web`, `spring-webmvc`, `springdoc`, or multiple `springdoc` artifacts.
6. If you have explicit `implementation("org.springframework:spring-web:...")` or similar overrides in `build.gradle.kts`, remove or align them to the Spring Boot-managed version.

Advanced: If a transitive dependency drags an incompatible version, add a `constraints` or `dependencyManagement` entry to force the correct version.

---

### 3) Circular bean dependency: `jwtAuthenticationFilter` <-> `securityConfig`
Symptom: Application context startup fails with a circular reference involving `jwtAuthenticationFilter` and `securityConfig` and suggests `spring.main.allow-circular-references=true` as a last resort.

Cause: Two beans depend on each other (directly or indirectly). For example, `SecurityConfig` needs the `JwtAuthenticationFilter` bean, and `JwtAuthenticationFilter` injects a bean from `SecurityConfig` (or injects the whole config). Circular references are discouraged and disabled by default in recent Spring Boot versions.

Fix options (pick one):
- Prefer creating the filter bean inside `SecurityConfig` as a method (factory) and do not inject `SecurityConfig` into the filter. Example:
  - In `SecurityConfig` define `@Bean fun jwtAuthFilter(): JwtAuthenticationFilter { return JwtAuthenticationFilter(tokenUtil, userDetailsService) }` and then `http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter::class.java)`.
- Inject only the minimal dependencies into `JwtAuthenticationFilter` (e.g., a `JwtService` and `UserDetailsService`) rather than `SecurityConfig` itself.
- Use `ObjectProvider<T>` or `Provider<T>` to lazily obtain a dependency, breaking the immediate construction cycle.

Do not enable `spring.main.allow-circular-references=true` unless you fully understand the implications.

---

### 4) IntelliJ `Could not autowire` warnings (false positives)
- Older IntelliJ versions sometimes don't fully recognise `@SpringBootApplication` meta-annotation and will show missing bean warnings even when the application runs fine. Using `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan` separately may silence the IDE.
- These are IDE inspections only — runtime success matters more. Update IntelliJ or install the latest Spring plugin to reduce false positives.

---

### 5) `Argument type mismatch: actual type is 'String?', but 'String' was expected.` (Kotlin nullability)
Cause: Kotlin distinguishes nullable (`String?`) and non-null (`String`) types. Spring may supply nullable values for environment variables or request fields.

Fix:
- Make constructor/parameter types nullable where appropriate (`String?`) or provide a default value.
- Validate and convert env values before passing to non-nullable parameters.

Example constructor fix:
```kotlin
data class RegisterDto(
  val email: String,
  val password: String,
  val fullName: String?, // optional
)
```

---

### 6) `UserDetails` constructor candidates not applicable
Symptom: Kotlin complains `None of the following candidates is applicable` for `UserDetails` constructors.

Cause: You're trying to instantiate a `UserDetails` implementation with the wrong argument types or missing defaults.

Fix: Add a matching constructor or provide factory function that maps from your `User`/DTO to the `UserDetails` type used by Spring Security. Example:

```kotlin
class AppUserDetails(
  private val fullName: String,
  private val phoneNumber: String? = null,
  private val address: String? = null,
  private val dateOfBirth: LocalDate? = null,
) : UserDetails {
  // ...implement methods
}
```

If you need a no-arg constructor for frameworks, add `constructor(): this("", null, null, null)` or use `@JvmOverloads`/`@NoArg` plugin as appropriate.

---

## Validation for Login and Registration DTOs
Use Jakarta Validation annotations (`jakarta.validation.constraints`) and ensure controllers accept `@Valid` DTOs.

Example DTOs:

```kotlin
data class LoginDto(
  @field:NotBlank
  @field:Email
  val email: String,

  @field:NotBlank
  @field:Size(min = 8)
  val password: String
)

data class RegisterDto(
  @field:NotBlank
  val fullName: String,

  @field:NotBlank
  @field:Email
  val email: String,

  @field:NotBlank
  @field:Size(min = 8)
  val password: String
)
```

Controller example:

```kotlin
@PostMapping("/register")
fun register(@Valid @RequestBody dto: RegisterDto, bindingResult: BindingResult): ResponseEntity<Any> {
    if (bindingResult.hasErrors()) {
        // return validation error map
    }
    // continue
}
```

Make sure you have `spring-boot-starter-validation` on the classpath.

---

## Repository note
You requested no `UserDetailsRepository.save(userDetails)` — that's fine. Typically `UserDetails` is a security-facing view of your domain `User` entity; persist the domain `User` only.

---

## Useful commands
- Refresh dependencies and build:

```bash
./gradlew clean build --refresh-dependencies
```

- Show runtime classpath to detect dependency conflicts:

```bash
./gradlew dependencies --configuration runtimeClasspath | sed -n '1,200p'
```

- Clear Gradle cache (if you suspect corrupt/old artifacts):

```bash
./gradlew --stop
rm -rf ~/.gradle/caches/
./gradlew clean build
```

---

## When to ask for help
If any of the following persist, please provide:
- `build.gradle.kts` (dependencies block)
- `SecurityConfig.kt` and `JwtAuthenticationFilter.kt` sources
- Exact `springdoc` dependency and Spring Boot version
- The `application.properties` or how you load `.env`

With those I can make targeted code edits to resolve autowire/circular-dependency or dependency-alignment issues.

---

## Contact
Author: nuwan konara

---

Generated on: 2025-12-02

