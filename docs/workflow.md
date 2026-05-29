# Contribution Workflow

## Branching

Create a new branch from `main` with a semantic prefix:

- `feat/<short-description>` — new feature.
- `fix/<short-description>` — bug fix.
- `ref/<short-description>` — refactor / cleanup.

## Commits

Use Conventional Commit style matching the repository history. The action verb signals issue closure on GitHub:

- `ref #35: extract validation logic from service` — ongoing work, **does not close** the issue.
- `fix #38: correct invoice balance calculation` — finishing work; GitHub will automatically close issue `#38` when merged.

Pick the verb deliberately: use `ref` until the issue is actually resolved.

## Pull requests

PRs must:

1. Reference the linked issue or ticket.
2. Fill in all sections of `.github/pull_request_template.md` (objective, what changed, how to test, additional context).
3. Include test evidence (command run and result).
4. Stay small — prefer under 20 changed files.
5. Target `main` and use **Squash and merge** only.

## Before opening a PR

```bash
./gradlew detekt
./gradlew test
```

Both must pass. Docker must be running for the integration tests inside `./gradlew test`.
