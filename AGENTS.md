# AGENTS.md — Supplement Tracker (project root)

Guidance for AI agents and human contributors working anywhere in this repository.
Subprojects have their own `AGENTS.md` with folder-specific rules:
[`backend/AGENTS.md`](backend/AGENTS.md) · [`frontend/AGENTS.md`](frontend/AGENTS.md) · [`desktop/AGENTS.md`](desktop/AGENTS.md)

## What this project is

Local-first supplement tracker (Phase 1: no account, no cloud, offline-first).

| Folder | Tech | Role |
|--------|------|------|
| `backend/` | Spring Boot 4.1, Java 21, Maven, H2 + Flyway | REST API + persistence; serves the Angular build in packaged mode |
| `frontend/` | Angular 22 (standalone, signals, OnPush), Angular Material, vitest | The single UI, built once for web/desktop (and Android via Capacitor later) |
| `desktop/` | Electron + electron-builder | Desktop shell: spawns the backend jar, opens `http://127.0.0.1:8090` |
| `docs/` | Markdown | Use cases (`use-cases.md`) and functional/non-functional requirements (`requirements.md`) |
| `scripts/` | Bash | `build-desktop.sh` — full desktop packaging pipeline |

Phase 2 (cloud sync, JWT auth) is **designed but not built**. Its seams — `deviceId`
and `updatedAt` columns, the swappable frontend data services, the auth-ready backend
structure — must never be removed or repurposed by Phase 1 work.

## MANDATORY workflow for every modification

1. **Never commit directly to `main` (or the default branch). Every change — code,
   docs, config — starts on a new branch**, named:
   - `feature/<short-description>` for new functionality
   - `fix/<short-description>` for bug fixes
   - `docs/<short-description>` for documentation-only changes
   - `chore/<short-description>` for tooling/build/dependency work
2. **Document in the same branch/PR as the change.** A PR is not complete unless it updates, where affected:
   - [`docs/use-cases.md`](docs/use-cases.md) — add/adjust the use case (actors,
     pre/postconditions, normal / alternate / exceptional flows) for any behaviour change
   - [`docs/requirements.md`](docs/requirements.md) — add/adjust functional (FR-x) and
     non-functional (NFR-x) requirements, keeping the traceability links valid
   - the relevant `AGENTS.md` file(s) — whenever conventions, commands, structure,
     ports, or gotchas change
   - the `README.md` API table — for any endpoint change
   - **code comments/Javadoc** on non-obvious logic — comments state constraints and
     intent, not change history
3. **Tests are part of the change**: backend `mvn test` and frontend `npm test` must be
   green before a PR is opened; new behaviour ships with new tests (see subproject
   AGENTS files for patterns).
4. Commit messages: imperative summary line, body explaining the why. One logical
   change per PR.

## Build & test quick reference

```bash
cd backend  && mvn test               # backend unit + integration tests
cd backend  && mvn spring-boot:run    # API on :8080, H2 file in ~/.supplement-tracker
cd frontend && npm start              # dev server on :4200, proxies /api -> :8080
cd frontend && npm test               # vitest
cd frontend && npx ng build           # production build
./scripts/build-desktop.sh            # full desktop package (jar + jlink JRE + electron-builder)
```

Node ≥ 22.22.3 is required by the Angular CLI. Java 21 and Maven for the backend.

## Cross-cutting rules

- The API contract is defined by the backend DTOs and mirrored by
  `frontend/src/app/core/models.ts` — change both sides together, in the same PR.
- Error responses always use the `ApiError` shape; new failure modes go through
  `GlobalExceptionHandler`, not ad-hoc response entities.
- Everything must keep working offline (NFR-1): no runtime CDN/font/script
  dependencies may be introduced.
- Benefits/health texts are informational; keep the "not medical advice"
  disclaimer wherever benefits are shown (NFR-10).
