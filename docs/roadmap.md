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

## Feature ideas — inspired by comparable apps & general-purpose use

Backlog of candidate features, grouped by the kind of app that proves they work.
Not scheduled: when one is picked up, move it into a prioritized section above,
give it a branch, and follow the mandatory documentation workflow.

### From medication trackers (Medisafe, MyTherapy)

- [ ] **Inventory / refill tracking** — pills-per-bottle count that decrements on each
      logged intake; "running low" warning at a configurable threshold, with the buy
      links (UC-04) one tap away — ties the two halves of the app together.
- [ ] **Flexible schedules** — beyond daily: every N days, specific weekdays,
      "as needed"; the Today checklist only shows what is due today.
- [ ] **Cycling protocols** — on/off cycles common for supplements (e.g. 8 weeks on /
      2 off): define the cycle, auto-pause tracking in off periods, show cycle status.
- [ ] **Skip with reason** — mark a supplement as intentionally skipped (sick, fasting,
      ran out) so adherence stats can distinguish "skipped" from "forgot".
- [ ] **Doctor report export** — PDF/CSV summary of what is taken, dosages, and
      adherence over a period, to hand to a doctor or pharmacist.
- [ ] **Interaction warnings** — flag known supplement–supplement conflicts (e.g.
      calcium impairs iron absorption; zinc–copper balance). Needs a curated local
      dataset; show as informational warnings, never medical advice (NFR-10).

### From nutrition apps (MyFitnessPal, Cronometer)

- [ ] **Elemental dose totals vs RDA** — store the elemental amount per intake (mg/IU)
      and show daily totals against reference intakes, warning above upper limits
      (e.g. vitamin D > 4000 IU/day). Needs dose fields on `Supplement` + a small
      RDA reference table.
- [ ] **Barcode scanning** — scan a product's EAN to create a custom supplement
      (Capacitor camera/ML Kit on Android; Open Food Facts as a free lookup source).
- [ ] **Product photo & label** — attach a photo of the actual bottle/label to a
      supplement so the checklist shows *your* product, not a generic name.
- [ ] **Intake notes** — optional note per log entry ("with breakfast", "felt nauseous")
      shown in History; the foundation for the correlation journal below.

### From habit trackers (Streaks, Loop, Habitica)

- [ ] **Streaks & adherence stats** — current/longest streak per supplement, 7/30-day
      adherence %; complements the stats item in Phase 1.x.
- [ ] **Calendar heatmap** — GitHub-style month/year grid of adherence on the History
      tab; instantly shows patterns (weekends missed, holiday gaps).
- [ ] **Partial goals** — "magnesium 5× a week is fine": weekly target counts instead
      of strict daily, with the progress bar tracking the week.
- [ ] **Home-screen widget & notification quick-log** (Android) — check off without
      opening the app: a checklist widget and "Taken ✓" action buttons on reminders.
- [ ] **Morning/evening stacks** — group supplements into named stacks checked off with
      one tap; the Today screen sections by stack (morning / with meals / bedtime).

### From health platforms (Apple Health, Google Fit / Health Connect)

- [ ] **Health Connect / HealthKit sync** — write intake events to the OS health store
      (Android Health Connect first, via Capacitor plugin) so other health apps can
      correlate them.
- [ ] **Wellbeing journal & correlations** — optional daily 1–5 ratings (sleep, energy,
      mood) charted against adherence per supplement over weeks: "did magnesium
      actually change my sleep?" — the question every supplement user has.

### General-purpose / quality of life

- [ ] **Cost tracking** — price + servings per container ⇒ cost per day/month per
      supplement and for the whole stack; pairs with inventory tracking.
- [ ] **Expiry dates** — expiry per bottle with a warning when close; useful for
      rarely-taken "as needed" supplements.
- [ ] **Multiple profiles** — separate tracked lists/logs for family members on the
      same device (schema: add a `profile` table + FK; keep single-profile UX default).
- [ ] **Onboarding goal wizard** — first-run flow: pick goals (sleep, energy, joints,
      immunity…) and get a suggested starter selection from the built-in catalog.
- [ ] **Fuzzy search & tags** — typo-tolerant catalog search; user-defined tags
      besides categories ("morning", "training days").
- [ ] **Archive instead of delete** — hide a custom supplement without losing its
      history (soft-delete flag, mirroring how untracking already preserves logs).
- [ ] **Backup reminders** — until Phase 2 sync exists, periodically prompt to run the
      JSON export (Phase 1.x item) so a lost phone doesn't mean lost history.

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
