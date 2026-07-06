# Running guide — how to run everything

Step-by-step instructions for every way of running the Supplement Tracker.

## 0. Prerequisites

| Tool | Version | Used for |
|------|---------|----------|
| Java (JDK) | 21+ | backend, jlink for desktop packaging |
| Maven | 3.9+ | backend build |
| Node.js | **≥ 22.22.3** (the Angular CLI enforces this) | frontend, Electron |
| npm | comes with Node | package management |
| Android Studio | Ladybug+ | only for the (future) Android build |

Check your versions: `java -version`, `mvn -version`, `node --version`.

## 1. Development mode (two terminals)

**Terminal 1 — backend** (API on http://localhost:8080):

```bash
cd backend
mvn spring-boot:run
```

First start creates the H2 database in `~/.supplement-tracker/` and Flyway seeds the
18-supplement catalog. Verify: `curl http://localhost:8080/api/supplements` → 18 entries.

**Terminal 2 — frontend** (UI on http://localhost:4200):

```bash
cd frontend
npm install        # first time only
npm start          # ng serve with proxy.conf.json → /api goes to :8080
```

Open http://localhost:4200 — changes to frontend code hot-reload; backend changes
need a restart of terminal 1.

## 2. Tests

```bash
cd backend  && mvn test    # unit + web-slice + integration tests (in-memory H2)
cd frontend && npm test    # vitest (jsdom, no browser required)
```

## 3. Production-style run (single process, like the desktop app uses)

The backend jar can serve the built frontend itself:

```bash
cd frontend && npx ng build
mkdir -p ../backend/src/main/resources/static
cp -r dist/frontend/browser/* ../backend/src/main/resources/static/
cd ../backend && mvn package -DskipTests
SUPPLEMENT_PORT=8090 java -jar target/supplement-tracker-backend-0.1.0.jar
```

Open http://localhost:8090 — one process serves UI + API.

## 4. Desktop app (Electron)

Quick dev run (uses the jar from step 3 and your system Java):

```bash
cd desktop
npm install
npm start
```

Full installer build (Angular build → jar → jlink JRE → electron-builder):

```bash
./scripts/build-desktop.sh
# installers land in desktop/dist/
```

The desktop app stores its database in Electron's per-user data directory
(e.g. `~/.config/supplement-tracker-desktop` on Linux), not in `~/.supplement-tracker`.

## 5. Android

Not built yet — the full plan is in
[`docs/android-integration.md`](android-integration.md).

## 6. Configuration reference

| Variable | Default | Meaning |
|----------|---------|---------|
| `SUPPLEMENT_PORT` | `8080` | HTTP port of the backend |
| `SUPPLEMENT_DATA_DIR` | `~/.supplement-tracker` | Directory holding the H2 database files |

## 7. Troubleshooting

- **`The file is locked: …supplements.mv.db`** — another backend instance is using the
  same data dir. Stop it (`pkill -f supplement-tracker-backend`) or use a different
  `SUPPLEMENT_DATA_DIR`.
- **Port already in use** — set `SUPPLEMENT_PORT` (and keep `frontend/proxy.conf.json`
  or `desktop/main.js` in sync if you change it permanently — see the AGENTS files).
- **Angular CLI refuses your Node** — it enforces ≥ 22.22.3; install a newer Node 22/24.
- **`WebMvcTest`/`AutoConfigureMockMvc` not found** after dependency changes — they live
  in the `spring-boot-webmvc-test` artifact in Spring Boot 4 (see `backend/AGENTS.md`).
- **Flyway didn't run / empty catalog** — ensure `spring-boot-starter-flyway` is on the
  classpath (bare `flyway-core` does nothing in Boot 4), and remember
  `src/test/resources/application.yml` *shadows* the main config in tests.
- **Frontend shows "Loading…" forever** — backend not running/reachable; check
  terminal 1 and the browser dev-tools network tab for `/api/...` failures.
