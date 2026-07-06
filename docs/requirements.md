# Requirements — Supplement Tracker

> Maintenance rule: any behaviour-changing PR MUST update this file and
> `docs/use-cases.md` in the same branch. See the root `AGENTS.md`.

## 1. Functional requirements (Phase 1)

| ID | Requirement | Use cases | Realised by |
|------|-------------|-----------|-------------|
| FR-1 | The system SHALL ship a built-in catalog of 18 supplements, each with description, typical dosage, category, an ordered benefits list, and where-to-buy links, loaded via a database seeder (Flyway), not hardcoded in the frontend. | UC-01, UC-03 | `backend/src/main/resources/db/migration/V2__seed_supplements.sql` |
| FR-2 | The system SHALL list the catalog (built-in + custom) alphabetically and mark which entries are tracked and which are custom. | UC-01 | `GET /api/supplements`, `CatalogComponent` |
| FR-3 | The system SHALL filter the catalog case-insensitively by name or category substring. | UC-02 | `SupplementRepository.search`, catalog search field |
| FR-4 | The system SHALL show a detail view per supplement with benefits, dosage, and buy options. | UC-03 | `GET /api/supplements/{id}`, `DetailComponent` |
| FR-5 | Buy options SHALL be templated store search URLs (Amazon, iHerb, eMAG, and Google Shopping — a cross-shop product aggregator — for built-ins) that open in the system browser. | UC-04 | seed migrations `V2`/`V3`, `LinkOpenerService` |
| FR-6 | The user SHALL be able to toggle "I take this" on/off per supplement; toggling off keeps intake history; toggling on again reactivates the same record. | UC-05, UC-06 | `POST/DELETE /api/user-supplements/{id}` |
| FR-7 | The user SHALL be able to create custom supplements (name required; category, dosage, description, benefits, buy links optional; blank category defaults to "Custom"). | UC-07 | `POST /api/supplements`, add-supplement dialog |
| FR-8 | The user SHALL be able to delete custom supplements after confirmation; built-in supplements SHALL NOT be deletable (409). Deletion cascades to tracking state and intake logs. | UC-08 | `DELETE /api/supplements/{id}`, confirm dialog, FK cascade |
| FR-9 | The Today screen SHALL show all actively tracked supplements as a checklist with a "N of M taken" progress indicator. | UC-09 | `TodayComponent`, `GET /api/user-supplements`, `GET /api/intake-logs?days=1` |
| FR-10 | Checking a supplement SHALL persist an intake log with a timestamp; unchecking SHALL delete that day's log. | UC-10, UC-11 | `POST /api/intake-logs`, `DELETE /api/intake-logs/{id}` |
| FR-11 | The History screen SHALL show the last 30 days of intakes grouped by day (newest first), each entry with its time. | UC-12 | `GET /api/intake-logs?days=30`, `HistoryComponent` |
| FR-12 | Intake logging SHALL be rejected for inactive/unknown tracked supplements (409/404) and for requests without a `userSupplementId` (400). | UC-10 | `IntakeLogService`, `GlobalExceptionHandler` |
| FR-13 | The desktop app SHALL bundle and manage the backend automatically: spawn on start, health-check before showing the window, kill on exit. | UC-13 | `desktop/main.js` |
| FR-14 | All error responses SHALL use a consistent JSON shape (`timestamp`, `status`, `error`, `message`, `details[]`) with correct HTTP status codes. | all | `ApiError`, `GlobalExceptionHandler` |

### Phase 2 (designed, NOT implemented — do not build without a dedicated branch/spec)

| ID | Requirement (future) |
|------|----------------------|
| FR-P2-1 | Optional account with JWT auth; fully local, accountless use remains supported. |
| FR-P2-2 | Delta sync via `POST /api/sync` (client sends changes since last sync timestamp; server returns its changes since then). |
| FR-P2-3 | Conflict resolution: intake logs merge additively by id (append-only events); `UserSupplement.active` uses last-write-wins by `updatedAt`. |
| FR-P2-4 | Schema seams already present: `deviceId` and `updatedAt` on `user_supplement` and `intake_log`. These columns MUST NOT be removed or repurposed. |

## 2. Non-functional requirements

| ID | Category | Requirement |
|------|----------|-------------|
| NFR-1 | Offline-first | Every Phase 1 feature except opening buy links SHALL work with no internet connection. Fonts and icons are self-hosted; no runtime CDN dependencies. |
| NFR-2 | Privacy | All data SHALL stay on the user's device (H2 file DB). No account, no telemetry, no external calls from the backend. |
| NFR-3 | Persistence | Data SHALL survive app restarts. Desktop stores under Electron `userData` (`SUPPLEMENT_DATA_DIR`); bare backend defaults to `~/.supplement-tracker`. |
| NFR-4 | Portability | One Angular codebase SHALL serve web/desktop (Electron) and, in a later phase, Android (Capacitor). Platform specifics stay behind service seams (`LinkOpenerService`, API services). |
| NFR-5 | Performance | Catalog and checklist interactions SHALL feel instant on local data (< 200 ms server handling for typical volumes; search debounced at 200 ms). |
| NFR-6 | Maintainability | Backend: layered Controller → Service → Repository, package-per-feature, DTO records, no entity leakage to the API. Frontend: standalone components, signals, `OnPush`, `inject()`, lazy routes. |
| NFR-7 | Testability | Backend changes SHALL keep unit + integration tests green (`mvn test`); frontend changes keep `npm test` (vitest) green. New behaviour ships with tests. |
| NFR-8 | Data integrity | Foreign keys with cascades; server-side validation on all writes; unique tracking row per supplement. |
| NFR-9 | Evolvability | Phase 2 seams (auth-ready structure, sync columns, swappable frontend data layer) MUST be preserved by every change. |
| NFR-10 | Content responsibility | Benefit texts are general information; the UI SHALL show a "not medical advice" disclaimer wherever benefits are displayed. |
| NFR-11 | Process | Every modification SHALL follow the branch-and-document workflow defined in the root `AGENTS.md` (new branch per change; use cases, requirements, agent files, and code comments updated in the same PR). |

## 3. Traceability

- Use case specifications: [`docs/use-cases.md`](use-cases.md)
- API surface: table in the root [`README.md`](../README.md)
- Contributor/agent workflow: root [`AGENTS.md`](../AGENTS.md) and per-subproject `AGENTS.md`
