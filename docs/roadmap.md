# Roadmap / TODO — next functionalities

> Workflow reminder (root `AGENTS.md`): each item below is implemented on its own
> branch (`feature/…`), and its PR must update `docs/use-cases.md`,
> `docs/requirements.md`, the affected `AGENTS.md` file(s), the README API table,
> and code comments. Tick items here in the same PR.

## Phase 1.x — polish of the local app

- [ ] **Intake reminders / notifications** — per-supplement time-of-day reminders.
      Desktop: Electron `Notification`; Android: Capacitor Local Notifications.
      Needs a `reminderTime` column (new Flyway migration) and a new UC.
- [ ] **Edit custom supplements** — currently only create/delete exist.
      `PUT /api/supplements/{id}` (custom only, 409 for built-ins), edit dialog reusing
      the add-supplement form.
- [ ] **Dosage schedule** — supplements taken more than once per day (e.g. 2×): target
      count per day, progress counts partial intakes, checklist shows "1/2".
- [ ] **Stats & streaks** — adherence percentage per supplement over 7/30 days, current
      streak, simple chart on the History tab.
- [ ] **Product shopping search (in-app)** — search "vitamin c" and get a list of
      concrete products from different producers/shops (see design notes below).
- [ ] **Data export / import** — JSON export of supplements + logs for backup and for
      migrating between devices before cloud sync exists; import with merge-by-id.
- [ ] **Edit past days** — check/uncheck intakes for previous days from the History tab
      (backend already accepts an explicit `takenAt`).
- [ ] **Romanian localisation** — extract UI strings, `@angular/localize` (en default, ro).
- [ ] **Dark mode** — Material theme pair + toggle (respect `prefers-color-scheme`).

## Phase 1.5 — Android target (Capacitor)

- [ ] **Capacitor shell** — wrap the Angular build for Android; see
      [`docs/android-integration.md`](android-integration.md) for the step-by-step plan.
- [ ] **Local data layer for Android** — SQLite implementation
      (`@capacitor-community/sqlite`) behind the three `core/` services; TS seed of the
      18-supplement catalog; port the business rules (reactivation, cascade delete,
      active-check on logging).
- [ ] **Capacitor Browser plugin** in `LinkOpenerService` for buy links.
- [ ] **Play-store packaging** — icons, splash, signing config, versioning.

## Phase 2 — cloud sync (design already fixed in docs/requirements.md FR-P2-*)

- [ ] **Accounts + JWT auth** (opt-in; accountless local use stays supported).
- [ ] **`POST /api/sync` delta endpoint** — client sends changes since last sync
      timestamp, server returns its changes since then.
- [ ] **Conflict resolution** — intake logs: additive merge by id;
      `UserSupplement.active`: last-write-wins by `updatedAt`.
- [ ] **Device management** — populate the existing `deviceId` columns.

## Tooling / process

- [ ] **CI pipeline** (GitHub Actions): `mvn test` + `npm test` + `ng build` on every PR;
      block merge on red.
- [ ] **Desktop installers in CI** — electron-builder matrix (win/mac/linux) on tags.
- [ ] **E2E smoke suite** — promote the Playwright script used during development into
      `frontend/e2e/` and run it against the packaged jar in CI.

---

## Design notes — product shopping search

**Today** the app ships *templated store search links* (UC-04): tapping "Amazon" on
Vitamin C opens Amazon's own search results in the browser. There is **no in-app
aggregated product list** ("many vitamin C products from different producers and
sites in one list") — that requires a product-data source. Options, by effort:

1. ~~**Zero-cost, no API — Google Shopping link**~~ ✅ **shipped** (migration
   `V3__add_google_shopping_links.sql`): every built-in supplement now has a fourth
   buy link `https://www.google.com/search?tbm=shop&q={query}`. Google aggregates
   products across shops and producers; opens in the browser, not in-app.
2. **Google Programmable Search JSON API** — needs an API key (free tier ≈ 100
   queries/day). Returns generic web results, not structured product cards; mediocre
   fit.
3. **SerpAPI / similar Google Shopping scraping APIs** — returns structured products
   (title, price, merchant, thumbnail, link) that could render as in-app cards.
   Paid after a small free tier; needs a proxy through our backend to keep the key
   out of the frontend.
4. **Per-merchant official APIs / affiliate feeds** — Amazon PA-API (requires an
   Associates account with sales), eBay Browse API, Profitshare/2Performant feeds for
   eMAG. Highest quality + potential affiliate revenue, highest integration and
   maintenance cost, one integration per shop.

Recommended path: ship (1) immediately, prototype (3) behind a backend endpoint
(`GET /api/shopping-search?q=`) so the API key stays server-side, cache results, and
mark the feature clearly as **online-only** (documented exception to NFR-1, like
buy links). Requires: new UC, FR update, README API row, and a config entry for the key.
