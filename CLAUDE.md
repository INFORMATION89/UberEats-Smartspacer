# Uber Eats for Smartspacer

## Versions

Versions follow MAJOR.MINOR.PATCH (semantic versioning):

- **MAJOR** (2.0.0): big updates, or changes that break how people already use the plugin
  (a setting removed, the card working differently enough that they'd need to set it up again)
- **MINOR** (1.3.0): new features or changes to how the plugin works
- **PATCH** (1.2.1): bug fixes and small adjustments

`versionName` in `app/build.gradle.kts` is the version, and `versionCode` is the same digits as
one number: MAJOR × 10000 + MINOR × 100 + PATCH (1.2.1 → 10201). Tags are `v` + the version
(`v1.2.1`); pushing a tag publishes a release (`.github/workflows/release.yml`).
Releases are signed with the permanent key shared with the other Smartspacer plugins, from the
`SIGNING_KEYSTORE_BASE64` and `SIGNING_PASSWORD` secrets (alias `smartspacer-plugins`).

## Releases

Never publish a release, push a version tag or change `.github/release-notes.md` for a release
without asking first. Show the version number and the full release notes, let me edit them,
and wait for my OK.
