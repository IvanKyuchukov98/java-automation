# Java Automation Framework

[![Tests](https://github.com/IvanKyuchukov98/java-automation/actions/workflows/tests.yml/badge.svg)](https://github.com/IvanKyuchukov98/java-automation/actions/workflows/tests.yml)
[![Allure report](https://img.shields.io/badge/report-Allure-orange)](https://ivankyuchukov98.github.io/java-automation/)
![Java 21](https://img.shields.io/badge/Java-21-blue)

UI and API test automation for the [Practice Software Testing "Toolshop"](https://practicesoftwaretesting.com) demo shop,
built with **Java 21, Cucumber, JUnit 5, Selenide and RestAssured**, with **Allure** reports.

**[View the latest test report](https://ivankyuchukov98.github.io/java-automation/)**

## Tech stack

| Area | Tool |
|---|---|
| Language | Java 21 |
| BDD | Cucumber 7 (Gherkin) |
| Test runner | JUnit 5 Platform |
| UI | Selenide 7 (driver managed automatically by Selenium Manager) |
| API | RestAssured 5, Jackson, JSON Schema validation |
| Assertions | AssertJ |
| Dependency injection | PicoContainer (per-scenario state) |
| Reports | Allure, Cucumber HTML |
| CI | GitHub Actions, GitHub Pages |

## What is tested

| Layer | Feature | Scenarios |
|---|---|---|
| UI | Login | Valid login, wrong password |
| UI | Product search | Matching results (outline), no-results message |
| UI | Product sorting | Price and name, ascending and descending (outlines) |
| UI | Product details | Details match the list, add to cart updates the badge |
| UI | Shopping cart | Contents, quantity change updates totals, remove item |
| UI + API | Checkout | Logged-in checkout with cash on delivery and credit card; the address auto-fill is verified against the postcode lookup API |
| API | Authentication | Token issued, 401 on wrong password |
| API | Products | Pagination, JSON schema, data rules |

## Design notes

- **No fixed sleeps.** Waits use Selenide conditions and the app's own loading-state
  attributes (e.g. `search_completed`).
- **Stable locators.** Page objects use the app's `data-test` attributes.
- **Isolated scenarios.** Every UI scenario gets a fresh browser; shared step state lives in
  per-scenario PicoContainer objects, never in static fields.
- **Negative login tests use unknown emails.** The site locks an account after repeated
  wrong passwords, so negative tests never target the shared demo accounts.
- **Fresh API requests.** Each API call builds a new request spec, so headers and bodies
  can't leak between calls.

## Project structure

```
src/test/java/com/qa/automation/
├── api/client/     API clients (fresh request per call, Allure logging)
├── api/models/     Request/response records
├── config/         Config loader (-D property > env var > config.properties)
├── context/        Per-scenario shared state
├── hooks/          Browser start/stop, failure screenshots
├── runner/         JUnit 5 Cucumber suite
├── steps/          Step definitions (api/, ui/)
├── ui/components/  Shared page parts (header)
├── ui/pages/       Page objects
└── utils/          Helpers (price parsing)
src/test/resources/
├── features/       Gherkin features (api/, ui/)
├── schemas/        JSON schemas
└── config.properties
```

## Running the tests

Requires JDK 21+, Maven 3.9+ and Chrome.

```bash
mvn clean test                                   # full regression suite
mvn clean test -Psmoke                           # smoke suite (critical path only)
mvn clean test -Dcucumber.filter.tags="@api"     # API only
mvn clean test -Dcucumber.filter.tags="@ui"      # UI only
mvn clean test -Dcucumber.filter.tags="@smoke and @ui"   # any tag expression
mvn clean test -Dbrowser.headless=true           # no visible browser
```

Any value in `config.properties` can be overridden with `-Dkey=value` or an environment variable
(`ui.base.url` → `UI_BASE_URL`).

## Continuous integration

GitHub Actions ([`.github/workflows/tests.yml`](.github/workflows/tests.yml)) runs the tests headless on Ubuntu:

| Trigger | Suite | Browsers |
|---|---|---|
| Nightly (03:00 UTC) | Full regression | Chrome, Firefox, Edge in parallel jobs |
| Manual (*Run workflow* button) | Choose smoke or regression | Choose one browser or all |

Each run writes a pass/fail summary to the run page and publishes the Allure report for every browser
to GitHub Pages. Failure screenshots are attached to the run as downloadable artifacts.

## Reports

```bash
mvn allure:serve       # build and open the Allure report
```

A plain Cucumber HTML report is also written to `target/cucumber-report.html`.

## Tags

| Tag | Meaning |
|---|---|
| `@ui` / `@api` | Test layer |
| `@smoke` | One critical-path check per feature area (8 scenarios). For outlines, only the `Smoke` examples block is tagged. |
| `@login`, `@search`, `@sorting`, `@product`, `@cart`, `@checkout`, `@auth`, `@products` | Feature area |
