package com.qa.automation.reporting;

import com.qa.automation.reporting.ScenarioTimingsPlugin.Summary;
import com.qa.automation.reporting.ScenarioTimingsPlugin.Timing;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static com.qa.automation.reporting.ScenarioTimingsPlugin.seconds;

/** Renders the scenario timings as a self-contained HTML page. */
final class TimingsPage {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC'");

    private TimingsPage() {
    }

    static String render(List<Timing> sorted, Summary summary) {
        long slowestMillis = sorted.isEmpty() ? 1 : Math.max(1, sorted.get(0).duration().toMillis());
        StringBuilder rows = new StringBuilder();
        int rank = 1;
        for (Timing timing : sorted) {
            double width = 100.0 * timing.duration().toMillis() / slowestMillis;
            rows.append("<tr>")
                    .append("<td class=\"rank\">").append(rank++).append("</td>")
                    .append("<td><div class=\"feature\">").append(escape(timing.feature())).append("</div>")
                    .append("<div>").append(escape(timing.scenario()))
                    .append(" <span class=\"line\">line ").append(timing.line()).append("</span></div></td>")
                    .append("<td><span class=\"status ").append(timing.status()).append("\">")
                    .append(timing.status()).append("</span></td>")
                    .append("<td class=\"duration\">").append(seconds(timing.duration())).append("</td>")
                    .append("<td class=\"bar-cell\"><div class=\"bar\" style=\"width:")
                    .append(String.format(Locale.ROOT, "%.1f", width)).append("%\"></div></td>")
                    .append("</tr>");
        }
        if (sorted.isEmpty()) {
            rows.append("<tr><td colspan=\"5\">No scenarios ran.</td></tr>");
        }

        return TEMPLATE
                .replace("{{date}}", DATE.format(ZonedDateTime.now(ZoneOffset.UTC)))
                .replace("{{elapsed}}", seconds(summary.elapsed()))
                .replace("{{total}}", seconds(summary.total()))
                .replace("{{scenarios}}", String.valueOf(summary.scenarios()))
                .replace("{{average}}", seconds(summary.average()))
                .replace("{{speedup}}", String.format(Locale.ROOT, "%.1f×", summary.speedUp()))
                .replace("{{slowest}}", sorted.isEmpty() ? "–" : seconds(sorted.get(0).duration()))
                .replace("{{rows}}", rows);
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static final String TEMPLATE = """
            <!doctype html>
            <html lang="en">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>Scenario Timings</title>
              <style>
                :root { --text: #1f2328; --muted: #59636e; --line: #d1d9e0; --card: #f6f8fa; --bar: #0969da;
                        --pass-bg: #dafbe1; --pass: #1a7f37; --fail-bg: #ffebe9; --fail: #cf222e; --bg: #fff; }
                @media (prefers-color-scheme: dark) {
                  :root { --text: #e6edf3; --muted: #9198a1; --line: #3d444d; --card: #151b23; --bar: #4493f8;
                          --pass-bg: #12261e; --pass: #3fb950; --fail-bg: #25171c; --fail: #f85149; --bg: #0d1117; }
                }
                body { font-family: system-ui, sans-serif; max-width: 64rem; margin: 2.5rem auto; padding: 0 1rem;
                       color: var(--text); background: var(--bg); }
                h1 { margin-bottom: .25rem; }
                .sub { color: var(--muted); margin-top: 0; }
                .cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(9rem, 1fr)); gap: .75rem;
                         margin: 1.5rem 0; }
                .card { background: var(--card); border: 1px solid var(--line); border-radius: 8px; padding: .75rem 1rem; }
                .card .value { font-size: 1.5rem; font-weight: 600; font-variant-numeric: tabular-nums; }
                .card .label { color: var(--muted); font-size: .85rem; }
                .table { overflow-x: auto; }
                table { border-collapse: collapse; width: 100%; }
                th, td { text-align: left; padding: .5rem; border-bottom: 1px solid var(--line); vertical-align: middle; }
                th { color: var(--muted); font-size: .85rem; font-weight: 600; }
                .rank, .line, .feature { color: var(--muted); }
                .feature, .line { font-size: .85rem; }
                .duration { font-variant-numeric: tabular-nums; text-align: right; white-space: nowrap; }
                .bar-cell { width: 25%; min-width: 6rem; }
                .bar { height: .6rem; border-radius: 3px; background: var(--bar); }
                .status { border-radius: 999px; padding: .1rem .5rem; font-size: .8rem; border: 1px solid var(--line); }
                .status.passed { background: var(--pass-bg); border-color: var(--pass); }
                .status.failed { background: var(--fail-bg); border-color: var(--fail); }
              </style>
            </head>
            <body>
              <h1>Scenario timings</h1>
              <p class="sub">Generated {{date}}. Durations include Background steps and hooks.</p>
              <div class="cards">
                <div class="card"><div class="value">{{elapsed}}</div><div class="label">Execution time (elapsed)</div></div>
                <div class="card"><div class="value">{{total}}</div><div class="label">Sum of scenario times</div></div>
                <div class="card"><div class="value">{{speedup}}</div><div class="label">Parallel speed-up</div></div>
                <div class="card"><div class="value">{{scenarios}}</div><div class="label">Scenarios</div></div>
                <div class="card"><div class="value">{{average}}</div><div class="label">Average</div></div>
                <div class="card"><div class="value">{{slowest}}</div><div class="label">Slowest</div></div>
              </div>
              <div class="table">
                <table>
                  <thead><tr><th>#</th><th>Scenario</th><th>Status</th><th>Duration</th><th></th></tr></thead>
                  <tbody>{{rows}}</tbody>
                </table>
              </div>
            </body>
            </html>
            """;
}
