# AGENTS.md — frontend/ (Angular 22)

Read the root [`AGENTS.md`](../AGENTS.md) first — its **branch-and-document workflow
is mandatory**: new branch per change; update `docs/use-cases.md`,
`docs/requirements.md`, this file, and code comments in the same PR as the code.

## Stack & layout

- Angular **22**, zoneless, standalone components only (no NgModules), Angular
  Material, vitest for unit tests. Node **≥ 22.22.3** required by the CLI.
- Structure under `src/app/`:
  - `core/` — `models.ts` (mirror of backend DTOs) and the thin HttpClient services:
    `SupplementsApiService`, `UserSupplementsService`, `IntakeLogService`,
    plus `LinkOpenerService`
  - `features/` — one folder per lazy route: `today/`, `catalog/` (+ add-supplement
    dialog), `detail/`, `history/`
  - `shared/` — reusable bits (`ConfirmDialogComponent`)
  - `app.routes.ts` — lazy `loadComponent` per tab; detail uses the `:id` param via
    `withComponentInputBinding` + `input.required`

## Non-negotiable conventions

- `ChangeDetectionStrategy.OnPush` on every component; `inject()` instead of
  constructor injection; signals for component state.
- Services return **Observables**; components convert with `toSignal()` at the
  consumption point. Refresh pattern: a `BehaviorSubject<void>` combined/switch-mapped
  into the data stream (see `TodayComponent`, `CatalogComponent`, `DetailComponent`).
- Typed reactive forms (`FormBuilder.nonNullable`); see the add-supplement dialog
  for the FormArray pattern.
- Templates use the built-in control flow (`@if`/`@for`), never `*ngIf`/`*ngFor`.

## Platform seams — preserve them

- Components must never touch `window`/Capacitor directly. External links go through
  `LinkOpenerService` (the Android build will swap in the Capacitor Browser plugin).
- All persistence flows through the three `core/` API services with **relative
  `/api` URLs** (dev proxy + same-origin packaged builds). The planned Android build
  replaces these with a local-SQLite implementation — keep them thin, no business
  logic in components or services beyond mapping.
- **Offline-first (NFR-1)**: fonts/icons are self-hosted via `@fontsource/*` imports
  in `styles.scss`. Never add CDN links to `index.html`.

## Commands

```bash
npm start        # dev server :4200, proxies /api to :8080 (proxy.conf.json)
npm test         # vitest (jsdom, no browser needed)
npx ng build     # production build to dist/frontend/browser
```

## Testing pattern

- Services: `provideHttpClient()` + `provideHttpClientTesting()` with
  `HttpTestingController` (see `supplements-api.service.spec.ts`).
- Components: `TestBed` with `provideRouter(routes)` + HTTP testing providers
  (see `app.spec.ts`).
- Keep `models.ts` in lockstep with backend DTOs — same PR as any backend contract change.
