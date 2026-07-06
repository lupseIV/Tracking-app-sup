# AGENTS.md — desktop/ (Electron shell)

Read the root [`AGENTS.md`](../AGENTS.md) first — its **branch-and-document workflow
is mandatory**: new branch per change; update `docs/use-cases.md` (UC-13 covers this
shell), `docs/requirements.md`, this file, and code comments in the same PR.

## How the desktop build works

- `main.js` (the whole shell) spawns the Spring Boot jar as a child process with
  `SUPPLEMENT_PORT=8090` and `SUPPLEMENT_DATA_DIR=<Electron userData>`, polls
  `GET /actuator/health` (~30 s budget), then opens a `BrowserWindow` on
  `http://127.0.0.1:8090/`. The jar serves both the API and the Angular build
  (copied into its static resources by `scripts/build-desktop.sh`).
- The backend child is killed on app quit; a backend crash surfaces an error dialog
  and quits (see UC-13 exceptional flows — keep the doc in sync with this behaviour).
- Packaging: `electron-builder` with `extraResources` = backend jar + a jlink-trimmed
  JRE (`desktop/jre/`, created by the build script). `findJava()` prefers the bundled
  JRE and falls back to system `java` for dev runs.

## Commands

```bash
# dev run (needs backend/target/*.jar; build it with: cd ../backend && mvn package)
npm install && npm start

# full installer pipeline from the repo root
../scripts/build-desktop.sh
```

## Rules

- Keep `nodeIntegration: false` and `contextIsolation: true`; the window loads only
  the local backend URL — never remote content.
- Port 8090 and the jar filename are referenced in `main.js`, `package.json`
  (`extraResources`), and `scripts/build-desktop.sh` — change them together, and
  update UC-13 in `docs/use-cases.md`.
- If the backend jar version changes (`backend/pom.xml` `<version>`), update the
  `extraResources.from` path and `findBackendJar()` accordingly.
- No auto-update, telemetry, or remote fetches — the desktop app must stay fully
  offline (NFR-1/NFR-2).
