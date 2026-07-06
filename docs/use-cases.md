# Use Cases — Supplement Tracker (Phase 1)

Scope: local-first, single-user application. There is one primary actor, the **User**
(the person tracking their supplements). The **Frontend** (Angular app) and the
**Local Backend** (Spring Boot REST API with H2 database) are supporting actors:
every use case below is realised by the Frontend calling the Backend API listed
in the *System interaction* line.

Phase 2 (cloud sync, optional account) will add a *Sync Server* actor; it is out
of scope here and only referenced where seams exist.

> Maintenance rule: any code change that adds, removes, or alters behaviour MUST
> update this file (and `docs/requirements.md`) in the same branch/PR. See the
> root `AGENTS.md` for the full workflow.

---

## UC-01 — Browse the supplement catalog

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | Application is running; database migrated and seeded (18 built-in supplements) |
| **Postconditions** | None (read-only); catalog displayed sorted by name |
| **System interaction** | `GET /api/supplements` |

**Normal flow**
1. User opens the *Supplements* tab.
2. Frontend requests the catalog from the backend.
3. Backend returns all supplements (built-in + custom) ordered by name, each with its category, custom flag, and tracked flag.
4. Frontend renders the list; tracked supplements show a check icon, custom ones a "Custom" label.

**Alternate flow**
- 4a. *Catalog contains custom supplements*: they appear inline, alphabetically sorted with the built-ins.

**Exceptional flow**
- 2a. *Backend unreachable*: the list stays in the "Loading…" state. User can retry by re-opening the tab (re-navigation re-triggers the request).

---

## UC-02 — Search the catalog

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | UC-01 available (catalog screen open) |
| **Postconditions** | None (read-only) |
| **System interaction** | `GET /api/supplements?search={text}` |

**Normal flow**
1. User types into the search field on the *Supplements* tab.
2. Frontend debounces the input (200 ms) and queries the backend.
3. Backend filters case-insensitively on **name or category** (substring match).
4. Frontend replaces the list with the matches.

**Alternate flow**
- 1a. *User clears the search text*: the full catalog is shown again (equivalent to UC-01).
- 3a. *Search matches a category* (e.g. "mineral"): all supplements of that category are returned even if their names don't match.

**Exceptional flow**
- 4a. *No matches*: an empty state is shown: “No supplements match "{text}".”

---

## UC-03 — View supplement details (benefits, dosage, where to buy)

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | Supplement exists (built-in or custom) |
| **Postconditions** | None (read-only) |
| **System interaction** | `GET /api/supplements/{id}` |

**Normal flow**
1. User taps a supplement in the catalog (or a row on the *Today* checklist).
2. Frontend navigates to `/supplements/{id}` and requests the detail.
3. Backend returns description, category, typical dosage, the ordered benefits list, the ordered buy links, custom flag, and tracked flag.
4. Frontend renders: header with "I take this" toggle, description, dosage card, benefits bullet list with a "not medical advice" disclaimer, and one tappable card per store.

**Alternate flow**
- 4a. *Custom supplement*: a "Delete custom supplement" button is additionally shown (see UC-08).
- 4b. *Supplement has no benefits / no buy links / no dosage*: the corresponding section is omitted.

**Exceptional flow**
- 3a. *Unknown id* (e.g. deleted in another window): backend returns **404 Not Found**; the screen stays in the loading state and the user navigates back.

---

## UC-04 — Open a "where to buy" link

| | |
|---|---|
| **Actors** | User (primary); System browser (supporting) |
| **Preconditions** | Detail screen open (UC-03); supplement has at least one buy link |
| **Postconditions** | External browser/tab opened with a pre-filled store search |
| **System interaction** | None (client-side only; URLs come from UC-03 payload) |

**Normal flow**
1. User taps a store card (Amazon / iHerb / eMAG, or a custom store).
2. Frontend delegates to `LinkOpenerService`, which opens the templated search URL in a new browser tab (`window.open`; on the future Android build this is the seam for the Capacitor Browser plugin).
3. The store's search results for that supplement appear in the external browser.

**Exceptional flow**
- 2a. *No internet connection*: the external browser shows its own offline error. The app itself is unaffected (buy links are the only feature that needs the internet).

---

## UC-05 — Start tracking a supplement ("I take this" on)

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | Detail screen open (UC-03); supplement currently not tracked |
| **Postconditions** | A `UserSupplement` row exists with `active = true`; supplement appears on the *Today* checklist; `updatedAt` is refreshed (Phase 2 seam) |
| **System interaction** | `POST /api/user-supplements/{supplementId}` → **201 Created** |

**Normal flow**
1. User switches the "I take this" toggle on.
2. Frontend calls the backend.
3. Backend creates a `UserSupplement` (addedDate = today, active = true) and returns it.
4. Frontend reloads the detail; the toggle reflects the tracked state; the supplement now appears in UC-09's checklist.

**Alternate flow**
- 3a. *Supplement was tracked before and toggled off*: the existing row is **reactivated** (`active = true`, `updatedAt` bumped) instead of creating a duplicate — intake history from the earlier period is preserved.

**Exceptional flow**
- 3b. *Unknown supplement id*: backend returns **404 Not Found**; UI state is unchanged.

---

## UC-06 — Stop tracking a supplement ("I take this" off)

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | Supplement is currently tracked (active `UserSupplement` exists) |
| **Postconditions** | `UserSupplement.active = false`; supplement removed from the *Today* checklist; **intake history is kept** |
| **System interaction** | `DELETE /api/user-supplements/{supplementId}` → **204 No Content** |

**Normal flow**
1. User switches the "I take this" toggle off on the detail screen.
2. Frontend calls the backend.
3. Backend marks the row inactive (soft delete) and bumps `updatedAt`.
4. Frontend reloads; the supplement disappears from the Today checklist but past intakes remain visible in History (UC-12).

**Exceptional flow**
- 3a. *Supplement was never tracked*: backend returns **404 Not Found**.

---

## UC-07 — Add a custom supplement

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | Catalog screen open (UC-01) |
| **Postconditions** | New `Supplement` row with `isCustom = true` persisted, including its benefits and buy links; visible in catalog and search |
| **System interaction** | `POST /api/supplements` → **201 Created** |

**Normal flow**
1. User taps the floating "+" button on the *Supplements* tab.
2. Frontend opens the "Add supplement" dialog (typed reactive form).
3. User fills in name (required), and optionally category, typical dosage, description, benefits (one per line), and store/link pairs.
4. User taps *Save*; frontend normalises the input (trims text, drops empty benefit lines and incomplete buy-link rows, prefixes `https://` on bare URLs) and posts it.
5. Backend validates (name non-blank, length limits), stores the supplement with `isCustom = true`, and returns it.
6. Dialog closes; catalog refreshes and shows the new supplement.

**Alternate flow**
- 3a. *Category left blank*: backend defaults it to `"Custom"`.
- 3b. *User adds/removes buy-link rows*: the form array grows/shrinks accordingly.

**Exceptional flow**
- 4a. *Name blank*: the Save button is disabled client-side; if bypassed, backend returns **400 Bad Request** with field-level details and the dialog stays open.
- 5a. *Validation fails server-side* (length limits): **400 Bad Request**; dialog stays open for correction.

---

## UC-08 — Delete a custom supplement

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | Detail screen of a **custom** supplement (UC-03, alt 4a) |
| **Postconditions** | Supplement row deleted; its `UserSupplement` and `IntakeLog` rows removed via FK cascade |
| **System interaction** | `DELETE /api/supplements/{id}` → **204 No Content** |

**Normal flow**
1. User taps "Delete custom supplement" on the detail screen.
2. Frontend shows a confirmation dialog warning that intake history is removed too.
3. User confirms.
4. Backend verifies the supplement is custom and deletes it (database cascades to tracking + logs).
5. Frontend navigates back to the catalog, which no longer lists the supplement.

**Alternate flow**
- 3a. *User cancels the confirmation*: nothing happens, dialog closes.

**Exceptional flow**
- 4a. *Supplement is built-in*: backend refuses with **409 Conflict** ("Built-in supplements cannot be deleted"). The delete button is not rendered for built-ins, so this only occurs via direct API access.
- 4b. *Unknown id*: **404 Not Found**.

---

## UC-09 — View today's checklist and progress

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | Application running |
| **Postconditions** | None (read-only) |
| **System interaction** | `GET /api/user-supplements` + `GET /api/intake-logs?days=1` |

**Normal flow**
1. User opens the *Today* tab (default route).
2. Frontend loads the active tracked supplements and today's intake logs in parallel.
3. Frontend renders the date header, a progress line ("N of M taken") with a progress bar, and one checkbox row per tracked supplement showing its name, typical dosage, and — if taken — the time it was logged.

**Alternate flow**
- 3a. *Nothing tracked yet*: an empty state explains how to enable "I take this" from the Supplements tab.

**Exceptional flow**
- 2a. *Backend unreachable*: screen stays in "Loading…" state.

---

## UC-10 — Log an intake (check off a supplement)

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | Today checklist shown (UC-09); supplement tracked and not yet checked today |
| **Postconditions** | `IntakeLog` row persisted with timestamp and denormalised date; progress bar and History updated |
| **System interaction** | `POST /api/intake-logs` with `{userSupplementId}` → **201 Created** |

**Normal flow**
1. User taps the checkbox of an unchecked supplement.
2. Frontend posts the intake; backend stamps `takenAt = now` (server time) and `logDate = today`, and returns the log.
3. Frontend refreshes the checklist: the row shows the checked state and the log time; the progress count increases.

**Alternate flow**
- 2a. *Client supplies an explicit `takenAt`* (future UI / API clients): backend uses it instead of "now" and derives `logDate` from it.

**Exceptional flow**
- 2b. *Tracking was toggled off meanwhile*: backend returns **409 Conflict** ("Supplement is not active"); frontend refresh removes the stale row.
- 2c. *User-supplement id unknown*: **404 Not Found**.
- 2d. *Missing `userSupplementId` in the request*: **400 Bad Request** with validation details.

---

## UC-11 — Undo an intake (uncheck)

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | Supplement already checked today (its `IntakeLog` id is known to the UI) |
| **Postconditions** | The `IntakeLog` row is deleted; progress and History updated |
| **System interaction** | `DELETE /api/intake-logs/{id}` → **204 No Content** |

**Normal flow**
1. User taps the checkbox of a checked supplement.
2. Frontend deletes today's log for that supplement.
3. Frontend refreshes: row unchecked, progress count decreases, the entry disappears from History.

**Exceptional flow**
- 2a. *Log already deleted* (e.g. second window): **404 Not Found**; the subsequent refresh brings the UI back in sync.

---

## UC-12 — View intake history

| | |
|---|---|
| **Actors** | User (primary); Local Backend (supporting) |
| **Preconditions** | Application running |
| **Postconditions** | None (read-only) |
| **System interaction** | `GET /api/intake-logs?days=30` |

**Normal flow**
1. User opens the *History* tab.
2. Frontend requests the last 30 days of intake logs.
3. Backend returns the logs joined with supplement names, **grouped by day, newest day first**, entries ordered by time.
4. Frontend renders a subheader per day and a row per intake with its timestamp (HH:mm).

**Alternate flow**
- 2a. *Different range* (API clients): `days` query parameter adjusts the window; values < 1 are treated as 1.

**Exceptional flow**
- 4a. *No logs in the window*: empty state explains that checking off supplements on Today populates History.

---

## UC-13 — Launch the desktop application

| | |
|---|---|
| **Actors** | User (primary); Electron shell & Local Backend (supporting) |
| **Preconditions** | Desktop build installed (or `backend/target/*.jar` present for a dev run) |
| **Postconditions** | Backend process running on port 8090 with data in the OS user-data directory; app window open |
| **System interaction** | Spawns `java -jar backend.jar`; polls `GET /actuator/health`; loads `http://127.0.0.1:8090/` |

**Normal flow**
1. User launches the app.
2. Electron main process locates the bundled JRE (falls back to system `java`) and the backend jar, and spawns the backend with `SUPPLEMENT_PORT=8090` and `SUPPLEMENT_DATA_DIR=<Electron userData>`.
3. Electron polls the health endpoint (up to ~30 s).
4. On `UP`, the window opens on the backend URL, which serves the Angular build and the API from the same origin.
5. When the user closes the window/app, Electron kills the backend child process.

**Exceptional flow**
- 2a. *Backend jar missing* (dev run without `mvn package`): error dialog explains how to build it; app quits.
- 3a. *Health check never succeeds*: "Startup failed" dialog; app quits.
- 4a. *Backend crashes while running*: Electron shows "Backend stopped (code N)" and quits.
