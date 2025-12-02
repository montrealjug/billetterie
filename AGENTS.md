# AGENTS

How to use AI/code agents effectively and safely in this project.

## Project context
- Java 21 / Spring Boot 3.5 webapp for Devoxx4KidsQC quarterly workshop ticketing.
- Authentication: no passwords; users receive personal links by email and use them for re-auth.
- Tech: JTE templating, QR code generation, Tailwind build (currently disabled for dev speed), Postgres via Docker/Testcontainers in tests, MailHog for dev email capture.
- Formatting: Spotless (`./mvnw spotless:apply`) is required; builds will fail on style drift.
- Generated artifacts live in `target/` and `jte-classes/`; keep them out of commits.

## Safe operating guidelines
- Protect user data: do not log, publish, or invent real emails/tokens/personal links. Use sample addresses and fake IDs in examples.
- Avoid network calls to production services. Prefer offline work; if email flows must be exercised, use the `local-mail` profile and MailHog.
- Do not create or commit secrets (RSA keys, SMTP creds, admin passwords). Reference env vars only.
- Steer clear of destructive git/database actions (resets, drops) unless explicitly requested. For local DB cleanup, use `docker-compose down` only when the user asks.
- Keep changes scoped; explain why edits are needed and where they live.

## Typical agent workflows
- **Read first**: `README.md` and `src/main/java/org/montrealjug/billetterie/email/README.md` cover running, profiles, and email behavior.
- **Run app locally**: `./mvnw spring-boot:run -Dspring-boot.run.profiles=local` (or `local-mail` to capture emails in MailHog). Requires Docker for supporting containers.
- **Build/test**: `./mvnw test` (uses Docker/Testcontainers). If only format fails, run `./mvnw spotless:apply`. For full checks before a PR, `./mvnw verify`.
- **Front-end assets**: Tailwind build is intentionally skipped. To enable/watch during dev: un-skip the `frontend-maven-plugin` in `pom.xml` and run `npm --prefix src/main/tailwind run watch` in a separate shell.
- **Email content**: New email types follow the conventions documented in `email/README.md`; use JTE templates and add tests/resources accordingly.
- **Where to put outputs**: keep temp files in `/tmp` or leave them untracked; do not write to `static-assets/` or other published assets unless the change is intentional.

## Good prompts and use cases
- Code changes: “Add booking confirmation copy explaining personal-link re-entry; update JTE templates and tests.”
- Debugging: “Investigate why after-booking email QR code fails when activity has no seats; reproduce with unit/integration test.”
- Ops docs: “Document how to rotate RSA signing key via env var `APP_RSA_KEY` without downtime.”
- UX/content: “Propose copy tweaks for guardian-facing emails; keep tone friendly for parents and kids.”

## Troubleshooting
- Spotless/style failures: `./mvnw spotless:apply`.
- Test failures due to services: ensure Docker is running; if Postgres/MailHog containers are stuck, restart Docker and rerun tests.
- Tailwind missing CSS: ensure the plugin is enabled and run the watch command; generated CSS lives under `static/`.
- Email rendering: use `local-mail` profile, send via `TestMailController`, view in MailHog, and iterate on JTE templates.

## Expectations for agent responses
- Be explicit about changes, files touched, and rationale.
- Default to minimal surface area edits and add brief code comments only when necessary to clarify non-obvious logic.
- Suggest follow-up actions (tests to run, configs to set) when relevant. Avoid committing large generated files or secrets.
