# Android integration guide (Capacitor)

How to turn the existing Angular app into a native Android app. **Not implemented
yet** — this is the agreed plan (Architecture Option A, see the root `AGENTS.md` and
`docs/requirements.md` NFR-4). Implement it on a `feature/android-capacitor` branch
and update the docs per the mandatory workflow.

## The architectural constraint (read first)

**Spring Boot cannot run on Android.** Android's ART runtime is not a standard JVM:
you cannot bundle a JRE, spawn a `java -jar` child process, or run an embedded HTTP
server from a Capacitor WebView app. Electron wraps Chromium+Node for desktop;
**Capacitor** is the correct tool for wrapping the same Angular build for Android.

Consequence: on Android, persistence lives **inside the Angular app** (SQLite via a
Capacitor plugin) behind the exact same service interfaces the HTTP implementations
use today. The backend remains the desktop engine and the future Phase 2 sync server.

## Step 1 — add Capacitor to the frontend

```bash
cd frontend
npm i @capacitor/core @capacitor/android
npm i -D @capacitor/cli
npx cap init "Supplement Tracker" com.lupseiv.supplementtracker --web-dir dist/frontend/browser
npx cap add android           # creates frontend/android/ (a Gradle project)
```

Commit `capacitor.config.ts` and the generated `android/` folder.

## Step 2 — abstract the data layer (the seam is already prepared)

Today the components inject three thin services that call HTTP:
`SupplementsApiService`, `UserSupplementsService`, `IntakeLogService`
(`frontend/src/app/core/`). Refactor them into injection tokens with two
implementations:

```
core/
├── supplements-api.ts        # abstract class (the token) — same method signatures
├── http/                     # current HttpClient implementations (desktop/web)
└── local/                    # NEW: SQLite implementations (Android)
```

- Provide per platform in `app.config.ts`:
  `Capacitor.isNativePlatform() ? LocalSupplementsService : HttpSupplementsService`.
- Components must not change at all — they already depend only on the service API
  and `models.ts`.

## Step 3 — local SQLite implementation

- Plugin: `@capacitor-community/sqlite`.
- Schema: mirror `backend/src/main/resources/db/migration/V1__schema.sql`
  (same tables/columns, including the Phase 2 `device_id`/`updated_at` seams).
- Seed: port `V2__seed_supplements.sql` to a TypeScript seed executed on first run
  (guard with a `schema_version` table so future migrations work the same way as
  Flyway: numbered, append-only).
- Port the business rules exactly (they are all in the backend services today):
  - activate = reactivate existing tracking row, bump `updated_at` (UC-05 alt 3a)
  - deactivate = soft delete, keep logs (UC-06)
  - delete custom only; cascade tracking + logs (UC-08)
  - intake logging rejects inactive supplements (UC-10 exc. 2b)
- Keep `models.ts` as the single contract; the local layer returns the same shapes
  (`tracked` flag computed with a join, history grouped by day, newest first).

## Step 4 — platform plugins

- `LinkOpenerService`: on native, use `@capacitor/browser` (`Browser.open({url})`)
  instead of `window.open` — the seam comment is already in the file.
- Later (roadmap): `@capacitor/local-notifications` for intake reminders.

## Step 5 — build & run

```bash
cd frontend
npx ng build
npx cap sync android      # copies dist + plugin config into android/
npx cap open android      # opens Android Studio
# then Run ▶ on an emulator or USB device
```

Command-line alternative: `cd frontend/android && ./gradlew assembleDebug`
(APK in `android/app/build/outputs/apk/debug/`).

## Step 6 — release checklist

- App icons + splash screen (`npx @capacitor/assets generate`).
- `minSdkVersion`: keep Capacitor's default (currently 23+) unless a plugin needs more.
- Signing config + keystore (never commit the keystore), Play Console setup.
- Version code/name aligned with the repo version.

## Testing strategy

- The local data layer gets its own vitest suite (run against sql.js or the plugin's
  web implementation) asserting the same behaviours the backend integration tests
  assert — reuse the use cases (UC-01…UC-12) as the test checklist.
- Manual smoke on device: seed present → track → log → history → custom CRUD →
  buy links open in the system browser.

## Documentation duties when implementing (mandatory workflow)

- New/updated use case entries where behaviour differs per platform.
- `docs/requirements.md`: mark NFR-4's Android row as delivered; add FRs for any
  Android-only behaviour.
- New `frontend/android/AGENTS.md` (or a section in `frontend/AGENTS.md`) with the
  Gradle/Capacitor commands and gotchas.
- Tick the roadmap items in `docs/roadmap.md`.
