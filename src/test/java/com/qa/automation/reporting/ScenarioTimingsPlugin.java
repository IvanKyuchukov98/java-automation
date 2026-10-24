package com.qa.automation.reporting;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import io.cucumber.plugin.event.TestSourceRead;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Records how long each scenario really takes (Background and hooks included) and the run's elapsed time,
 * then writes an HTML page with every scenario, slowest first, and prints a short summary.
 * Safe for parallel runs. Register as {@code com.qa.automation.reporting.ScenarioTimingsPlugin:<output.html>}.
 */
public class ScenarioTimingsPlugin implements ConcurrentEventListener {

    private static final Pattern FEATURE_NAME = Pattern.compile("^\\s*Feature:\\s*(.+)$", Pattern.MULTILINE);
    private static final int SLOWEST_IN_CONSOLE = 5;

    private final File output;
    private final Map<URI, String> featureNames = new ConcurrentHashMap<>();
    private final Map<TestCase, Instant> startTimes = new ConcurrentHashMap<>();
    private final List<Timing> timings = new ArrayList<>();
    private Instant runStarted;

    public ScenarioTimingsPlugin(File output) {
        this.output = output;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestRunStarted.class, event -> runStarted = event.getInstant());
        publisher.registerHandlerFor(TestSourceRead.class, this::rememberFeatureName);
        publisher.registerHandlerFor(TestCaseStarted.class,
                event -> startTimes.put(event.getTestCase(), event.getInstant()));
        publisher.registerHandlerFor(TestCaseFinished.class, this::recordTiming);
        publisher.registerHandlerFor(TestRunFinished.class, this::report);
    }

    private void rememberFeatureName(TestSourceRead event) {
        Matcher matcher = FEATURE_NAME.matcher(event.getSource());
        featureNames.put(event.getUri(), matcher.find() ? matcher.group(1).trim() : event.getUri().toString());
    }

    private void recordTiming(TestCaseFinished event) {
        TestCase testCase = event.getTestCase();
        Instant started = startTimes.remove(testCase);
        Duration duration = started == null ? event.getResult().getDuration()
                : Duration.between(started, event.getInstant());
        Timing timing = new Timing(
                featureNames.getOrDefault(testCase.getUri(), testCase.getUri().toString()),
                testCase.getName(),
                testCase.getLocation().getLine(),
                event.getResult().getStatus().name().toLowerCase(Locale.ROOT),
                duration);
        synchronized (timings) {
            timings.add(timing);
        }
    }

    private void report(TestRunFinished event) {
        List<Timing> sorted;
        synchronized (timings) {
            sorted = new ArrayList<>(timings);
        }
        sorted.sort(Comparator.comparing(Timing::duration).reversed());

        Duration elapsed = runStarted == null ? Duration.ZERO : Duration.between(runStarted, event.getInstant());
        Duration total = sorted.stream().map(Timing::duration).reduce(Duration.ZERO, Duration::plus);
        Summary summary = new Summary(elapsed, total, sorted.size());

        writeHtml(sorted, summary);
        printSummary(sorted, summary);
    }

    private void writeHtml(List<Timing> sorted, Summary summary) {
        try {
            File parent = output.getAbsoluteFile().getParentFile();
            if (parent != null) {
                Files.createDirectories(parent.toPath());
            }
            Files.writeString(output.toPath(), TimingsPage.render(sorted, summary), StandardCharsets.UTF_8);
            // Machine-readable totals next to the page, e.g. for CI summaries.
            String json = String.format(Locale.ROOT,
                    "{\"elapsedSeconds\": %.1f, \"totalSeconds\": %.1f, \"scenarios\": %d}%n",
                    summary.elapsed().toMillis() / 1000.0, summary.total().toMillis() / 1000.0, summary.scenarios());
            Files.writeString(new File(output.getPath().replaceFirst("\\.html?$", "") + ".json").toPath(),
                    json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + output, e);
        }
    }

    private void printSummary(List<Timing> sorted, Summary summary) {
        StringBuilder text = new StringBuilder()
                .append(System.lineSeparator())
                .append("Execution time: ").append(seconds(summary.elapsed())).append(" elapsed, ")
                .append(seconds(summary.total())).append(" summed over ").append(summary.scenarios())
                .append(" scenarios").append(System.lineSeparator())
                .append("Slowest scenarios:").append(System.lineSeparator());
        sorted.stream().limit(SLOWEST_IN_CONSOLE).forEach(timing -> text
                .append(String.format(Locale.ROOT, "  %8s  %s > %s%n",
                        seconds(timing.duration()), timing.feature(), timing.scenario())));
        text.append("Full list: ").append(output.getPath());
        System.out.println(text);
    }

    static String seconds(Duration duration) {
        return String.format(Locale.ROOT, "%.1fs", duration.toMillis() / 1000.0);
    }

    record Timing(String feature, String scenario, int line, String status, Duration duration) {
    }

    record Summary(Duration elapsed, Duration total, int scenarios) {

        Duration average() {
            return scenarios == 0 ? Duration.ZERO : total.dividedBy(scenarios);
        }

        /** How many times faster than running the scenarios one after another. */
        double speedUp() {
            return elapsed.isZero() ? 1 : (double) total.toMillis() / elapsed.toMillis();
        }
    }
}
