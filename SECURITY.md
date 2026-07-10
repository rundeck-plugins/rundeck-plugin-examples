# Security Policy

## Repository Classification

This repository contains **buildable Rundeck plugin examples** intended to be used as
starting points for real plugin development.

### Repository Contents
- Rundeck plugin examples in various languages (Java, Groovy, scripts)
- Real Gradle and Maven build projects with declared dependencies
- Documentation, README files, and example configurations

### Security Posture

Because these examples are meant to be copied and extended by plugin authors, they
should model **current, patched dependency versions**. When a security scan reports a
vulnerability in an example project, the goal is to **fix it** (upgrade or override the
affected dependency) rather than suppress it, so the examples always demonstrate safe
dependency choices.

## Security Scanning

### Snyk Security Scanning

Each example project is scanned by Snyk as part of the org-wide security workflow
(`.github/workflows/snyk-scan.yml`, which calls the reusable
`rundeck-plugins/.github` workflow). The scan runs `snyk test --all-projects` against
the Gradle and Maven manifests in each `example-*/` directory.

**How findings are handled:**
- **Fix first.** Prefer upgrading the affected dependency, or excluding a vulnerable
  transitive dependency and replacing it with a patched one (for example, replacing
  `commons-lang:commons-lang` with `commons-lang3`).
- **Transitive dependencies from `rundeck-core`** are provided by the Rundeck runtime
  (declared `compileOnly`), so they are not shipped by the plugin. Where they can be
  overridden to a patched version, do so; otherwise the finding reflects `rundeck-core`
  rather than the example itself.
- **Ignoring findings.** The Snyk organization does not honor local `.snyk` policy
  files, so ignores are not managed in-repo. If a finding is genuinely upstream-only
  and cannot be fixed locally, record the ignore in the Snyk UI at the project level
  with a justification and expiry.

### Security Guidelines

#### For Repository Users
1. **Example Code Usage**: All plugin examples are for educational purposes. Review and
   adapt them to your security requirements before any production use.
2. **Dependency Management**: Keep dependencies current and scan any plugin you build
   from these examples.
3. **Code Review**: Always perform a security review before deploying a plugin to a
   Rundeck production environment.

## Reporting Security Issues

1. **For Example Code Issues**: Open an issue in this repository describing the concern.
2. **For Rundeck Core Security Issues**: Follow the
   [Rundeck Security Policy](https://github.com/rundeck/rundeck/security/policy).
3. **For Production Plugin Security**: Contact your organization's security team.

## Security Best Practices for Plugin Development

When developing Rundeck plugins based on these examples:

1. **Input Validation**: Always validate user inputs and configuration parameters.
2. **Credential Management**: Use Rundeck's secure credential storage features.
3. **Error Handling**: Avoid exposing sensitive information in error messages.
4. **Logging**: Be cautious about logging sensitive data.
5. **Dependencies**: Keep all dependencies up to date and scan for vulnerabilities.
6. **Testing**: Include security testing in your plugin development process.

## Additional Resources

- [Rundeck Plugin Development Guidelines](https://docs.rundeck.com/docs/developer)
- [Rundeck Security Documentation](https://docs.rundeck.com/docs/administration/security)
- [OWASP Security Guidelines](https://owasp.org/)
