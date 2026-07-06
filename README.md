# Supplement Tracker

Full-stack supplement tracking app. **Phase 1: fully local, offline-first, no account required.**

- **Backend:** Spring Boot 4 (Java 21), REST API, H2 file database, Flyway migrations
- **Frontend:** Angular 22 — standalone components, signals, OnPush, lazy routes, Angular Material
- **Desktop:** Electron shell that spawns the bundled backend jar
- **Android (planned):** Capacitor wrapping the same Angular build, with a local SQLite
  implementation behind the same service interfaces (Spring Boot cannot run on Android)

## Features

- **Today** — checklist of the supplements you take, progress bar ("3 of 5 taken"),
  tap-to-log with timestamp, tap again to undo
- **Supplements** — searchable catalog of 18 built-ins (seeded via Flyway) + your own
  custom supplements (FAB → dialog)
- **Detail** — description, typical dosage, benefits list, "where to buy" cards
  (Amazon / iHerb / eMAG templated search URLs) opening in the system browser,
  "I take this" toggle, delete (custom supplements only)
- **History** — last 30 days grouped by day, each entry with its timestamp

## Repository layout

```
backend/    Spring Boot 4 REST API (package-per-feature: supplement, usersupplement, intakelog)
frontend/   Angular 22 app (core/ services+models, features/ lazy routes, shared/)
desktop/    Electron main process + electron-builder config
scripts/    build-desktop.sh — full desktop packaging pipeline
```

## Development

Backend (port 8080, H2 file DB in `~/.supplement-tracker`):

```bash
cd backend && mvn spring-boot:run
```

Frontend dev server (port 4200, proxies `/api` to 8080):

```bash
cd frontend && npm install && npm start
```

Tests:

```bash
cd backend && mvn test          # unit + controller integration tests (in-memory H2)
cd frontend && npm test         # vitest
```

## Desktop build (Electron)

```bash
./scripts/build-desktop.sh
```

This builds the Angular app, copies it into the backend's static resources (the jar
serves the SPA), packages the jar, creates a trimmed JRE with `jlink`, and runs
`electron-builder`. The Electron main process spawns the jar (data dir = Electron
`userData`), waits for `/actuator/health`, then opens the window at `http://127.0.0.1:8090`.

For a quick unpackaged run: `cd backend && mvn package`, then `cd desktop && npm install && npm start`.

## API

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/supplements?search=` | Catalog with optional name/category filter |
| GET | `/api/supplements/{id}` | Detail incl. benefits + buy links + tracked flag |
| POST | `/api/supplements` | Create custom supplement (201) |
| DELETE | `/api/supplements/{id}` | Delete custom (204); built-ins → 409 |
| GET | `/api/user-supplements` | Active "I take this" checklist |
| POST | `/api/user-supplements/{supplementId}` | Toggle on (reactivates existing row) |
| DELETE | `/api/user-supplements/{supplementId}` | Toggle off (history kept) |
| POST | `/api/intake-logs` | Log an intake `{userSupplementId, takenAt?}` (201) |
| GET | `/api/intake-logs?days=30` | History grouped by day, newest first |
| DELETE | `/api/intake-logs/{id}` | Undo a logged intake |

Benefits shown in the app are general information, not medical advice.

## Phase 2 seams (cloud sync — designed, not built)

- `UserSupplement` and `IntakeLog` already carry `deviceId` + `updatedAt` columns.
- Planned conflict strategy: intake logs are append-only events → additive merge by id;
  `UserSupplement.active` toggles → last-write-wins by `updatedAt`.
- Planned endpoint: `POST /api/sync` delta exchange (client sends changes since last sync
  timestamp, server returns its changes since then). Auth will be opt-in JWT; local-only
  use stays fully supported.
- `LinkOpenerService` (frontend) is the seam for the Capacitor Browser plugin on Android;
  the three API services are the seam for a local-SQLite implementation on Android.
