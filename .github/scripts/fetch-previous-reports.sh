#!/usr/bin/env bash
# Downloads the "reports-*" artifacts of earlier runs of this workflow into <runs-dir>/<run number>/,
# until <runs-dir> holds <max-runs> runs (the current run is expected to be there already).
# Runs whose artifacts have expired or never existed are skipped.
#
# Usage: fetch-previous-reports.sh <runs-dir> <max-runs>
# Needs: gh CLI, GH_TOKEN with actions:read, GITHUB_REPOSITORY, GITHUB_RUN_ID.
set -euo pipefail

runs_dir="$1"
max_runs="$2"
mkdir -p "$runs_dir"

count_runs() { find "$runs_dir" -mindepth 1 -maxdepth 1 -type d | wc -l; }

gh run list --repo "$GITHUB_REPOSITORY" --workflow tests.yml --branch main --status completed \
    --limit 50 --json databaseId,number --jq '.[] | "\(.databaseId) \(.number)"' |
while read -r id number; do
  [ "$(count_runs)" -lt "$max_runs" ] || break
  [ "$id" != "$GITHUB_RUN_ID" ] || continue

  tmp="$(mktemp -d)"
  if gh run download "$id" --repo "$GITHUB_REPOSITORY" --pattern 'reports-*' --dir "$tmp" 2>/dev/null; then
    dest="$runs_dir/$number"
    mkdir -p "$dest"
    # gh puts each artifact in a folder named after it; each artifact holds <browser>/...
    for artifact in "$tmp"/reports-*/; do
      [ -d "$artifact" ] && cp -r "$artifact". "$dest/"
    done
    echo "Run #$number: added"
  else
    echo "Run #$number: no reports (expired or none uploaded)"
  fi
  rm -rf "$tmp"
done
