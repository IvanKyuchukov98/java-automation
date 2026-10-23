# Java Automation Framework

UI and API test automation for the [Practice Software Testing "Toolshop"](https://practicesoftwaretesting.com) demo shop,
built with **Java 21, Cucumber, JUnit 5, Selenide and RestAssured**, with **Allure** reports.

> Work in progress. More features, CI and a published report are coming.

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
mvn clean test                                   # everything
mvn clean test -Dcucumber.filter.tags="@api"     # API only
mvn clean test -Dcucumber.filter.tags="@ui"      # UI only
mvn clean test -Dcucumber.filter.tags="@smoke"   # smoke set
mvn clean test -Dbrowser.headless=true           # no visible browser
```

Any value in `config.properties` can be overridden with `-Dkey=value` or an environment variable
(`ui.base.url` → `UI_BASE_URL`).

## Reports

```bash
mvn allure:serve       # build and open the Allure report
```

A plain Cucumber HTML report is also written to `target/cucumber-report.html`.

## Tags

| Tag | Meaning |
|---|---|
| `@ui` / `@api` | Test layer |
| `@smoke` | Fast, critical-path checks |
| `@login`, `@search`, `@sorting`, `@product`, `@cart`, `@checkout`, `@auth`, `@products` | Feature area |
