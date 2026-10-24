#!/usr/bin/env bash
# Builds <site>/index.html: a table of the runs in <site>/runs/<run number>/<browser>/, newest first,
# using the run.env file each browser report carries.
#
# Usage: build-report-index.sh <site-dir>
set -euo pipefail

site="$1"
repo_url="${GITHUB_SERVER_URL:-https://github.com}/${GITHUB_REPOSITORY:-}"
rows=""

for run_dir in $(find "$site/runs" -mindepth 1 -maxdepth 1 -type d -printf '%f\n' 2>/dev/null | sort -rn); do
  date="" suite="" sha="" run_id="" browsers=""
  for browser_dir in "$site/runs/$run_dir"/*/; do
    [ -d "$browser_dir" ] || continue
    browser="$(basename "$browser_dir")"
    RAN="" FAILED=""
    if [ -f "$browser_dir/run.env" ]; then
      # shellcheck disable=SC1091 # written by this workflow's test job
      source "$browser_dir/run.env"
      date="$DATE" suite="$SUITE" sha="$SHA" run_id="$RUN_ID"
    fi
    if [ -z "$RAN" ]; then
      result="" status="unknown"
    elif [ "$FAILED" -gt 0 ]; then
      result="$((RAN - FAILED))/$RAN" status="failed"
    else
      result="$RAN/$RAN" status="passed"
    fi
    browsers="$browsers<span class=\"browser $status\"><a href=\"runs/$run_dir/$browser/\">$browser</a>"
    [ -n "$result" ] && browsers="$browsers <span class=\"count\">$result</span>"
    browsers="$browsers <a class=\"allure\" href=\"runs/$run_dir/$browser/allure/\">Allure</a></span>"
  done

  [ -n "$browsers" ] || continue

  run_cell="#$run_dir"
  [ -n "$run_id" ] && run_cell="<a href=\"$repo_url/actions/runs/$run_id\">#$run_dir</a>"
  commit_cell=""
  [ -n "$sha" ] && commit_cell="<a href=\"$repo_url/commit/$sha\"><code>${sha:0:7}</code></a>"

  rows="$rows<tr><td>$run_cell</td><td>${date:-}</td><td>${suite:-}</td><td>$commit_cell</td><td><div class=\"browsers\">$browsers</div></td></tr>"
done

[ -n "$rows" ] || rows='<tr><td colspan="5">No reports yet.</td></tr>'

cat > "$site/index.html" <<EOF
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Test Reports</title>
  <style>
    :root { --text: #1f2328; --muted: #59636e; --line: #d1d9e0; --link: #0969da;
            --pass-bg: #dafbe1; --pass: #1a7f37; --fail-bg: #ffebe9; --fail: #cf222e; --bg: #fff; }
    @media (prefers-color-scheme: dark) {
      :root { --text: #e6edf3; --muted: #9198a1; --line: #3d444d; --link: #4493f8;
              --pass-bg: #12261e; --pass: #3fb950; --fail-bg: #25171c; --fail: #f85149; --bg: #0d1117; }
    }
    body { font-family: system-ui, sans-serif; max-width: 60rem; margin: 2.5rem auto; padding: 0 1rem;
           color: var(--text); background: var(--bg); }
    h1 { margin-bottom: .25rem; }
    p { color: var(--muted); margin-top: 0; }
    a { color: var(--link); }
    .table { overflow-x: auto; }
    table { border-collapse: collapse; width: 100%; }
    th, td { text-align: left; padding: .6rem .5rem; border-bottom: 1px solid var(--line); white-space: nowrap; }
    th { color: var(--muted); font-weight: 600; font-size: .9rem; }
    .browsers { display: flex; gap: .5rem; flex-wrap: wrap; }
    td:last-child { white-space: normal; }
    .browser { border-radius: 999px; padding: .15rem .6rem; border: 1px solid var(--line); }
    .browser.passed { background: var(--pass-bg); border-color: var(--pass); }
    .browser.failed { background: var(--fail-bg); border-color: var(--fail); }
    .count { color: var(--muted); font-size: .85rem; }
    .allure { font-size: .8rem; }
  </style>
</head>
<body>
  <h1>Test reports</h1>
  <p>Last runs of the <a href="$repo_url">java-automation</a> suite, newest first.
     Each browser opens its Cucumber report; "Allure" opens the detailed report.</p>
  <div class="table">
    <table>
      <thead><tr><th>Run</th><th>Date</th><th>Suite</th><th>Commit</th><th>Browsers (passed / run)</th></tr></thead>
      <tbody>$rows</tbody>
    </table>
  </div>
</body>
</html>
EOF
