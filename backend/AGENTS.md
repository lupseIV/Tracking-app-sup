# AGENTS.md — backend/ (Spring Boot 4)

Read the root [`AGENTS.md`](../AGENTS.md) first — its **branch-and-document workflow
is mandatory**: new branch per change; update `docs/use-cases.md`,
`docs/requirements.md`, this file, the README API table, and code comments in the
same PR as the code.

## Stack & layout

- Spring Boot **4.1** (Java 21, Maven), H2 file database, Flyway migrations.
- Package-per-feature under `com.lupseiv.supplements`:
  - `supplement/` — catalog entity (`Supplement`, embeddable `BuyLink`), repository, service, controller, `dto/`
  - `usersupplement/` — the "I take this" tracking state
  - `intakelog/` — intake events + history grouping
  - `common/` — `ApiError`, `NotFoundException` (404), `ConflictException` (409), `GlobalExceptionHandler`
  - `config/` — dev CORS for `http://localhost:4200`
- Layering: Controller → Service → Repository. Controllers hold no logic; services
  own transactions (`@Transactional`, `readOnly` for queries); **entities never leave
  the service layer** — API types are DTO records with static `from(...)` mappers.

## Commands

```bash
mvn test              # all tests (surefire also picks up *IT.java — configured in pom.xml)
mvn spring-boot:run   # :8080, H2 file in ~/.supplement-tracker (SUPPLEMENT_DATA_DIR / SUPPLEMENT_PORT override)
mvn package           # jar; serves frontend build if copied into src/main/resources/static (gitignored)
```

## Database & migrations

- Schema is owned by **Flyway** (`src/main/resources/db/migration/`); Hibernate runs
  `ddl-auto: validate` only. **Never edit an applied migration** — add a new
  `V<n>__description.sql`.
- Seed catalog lives in `V2__seed_supplements.sql` (explicit ids 1–18, identity
  restarted at 100). Catalog content changes are made by a **new** migration.
- **Phase 2 seams — do not touch**: `device_id` and `updated_at` on `user_supplement`
  and `intake_log` are reserved for cloud sync (see `docs/requirements.md` FR-P2-*).
  `UserSupplement.setActive()` must keep bumping `updatedAt`.
- Deletes rely on `ON DELETE CASCADE` (supplement → user_supplement → intake_log).

## Testing pattern

- Service unit tests: JUnit 5 + `@ExtendWith(MockitoExtension.class)`, `@Mock`/`@InjectMocks`.
- Web-slice tests: `@WebMvcTest` + `@MockitoBean` (e.g. `SupplementControllerTest`).
- Integration tests: extend `BaseControllerIntegrationTest` (`@SpringBootTest` +
  `@AutoConfigureMockMvc` + `@Transactional`, Flyway-seeded in-memory H2) and name
  them `*IT.java`.
- `src/test/resources/application.yml` **shadows** (does not merge with) the main
  one — keep it complete when adding config.

## Spring Boot 4 gotchas (cost us time once already)

- Test slices moved: `WebMvcTest`/`AutoConfigureMockMvc` come from the
  `spring-boot-webmvc-test` artifact, package `org.springframework.boot.webmvc.test.autoconfigure`.
- Flyway auto-configuration requires `spring-boot-starter-flyway` (bare `flyway-core`
  silently does nothing).

## API conventions

- Routes under `/api/<plural-noun>`; 201 for creates, 204 for deletes,
  404 unknown id, 409 business-rule violation, 400 validation (details from
  `GlobalExceptionHandler`). Any new failure mode goes through the handler.
- Every endpoint change: update the README API table + `docs/use-cases.md` +
  `frontend/src/app/core/models.ts` and services in the same PR.
