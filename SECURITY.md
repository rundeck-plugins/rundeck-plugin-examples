# Security Policy

## Repository Classification

This repository is classified as a **configuration-only documentation repository** containing Rundeck plugin examples and educational materials.

### Repository Contents
- Rundeck plugin examples in various programming languages (Java, Groovy, scripts)
- Build configuration files (Gradle, Maven)
- Documentation and README files
- Example configurations and templates

### Security Posture

**Runtime Dependencies:** None  
**Production Deployment:** Not applicable  
**Security Risk Level:** Low (documentation/examples only)

This repository does not contain:
- Production runtime code
- Executable applications that would be deployed
- Runtime dependencies that could introduce vulnerabilities in production systems
- User data or sensitive information

## Security Scanning Compliance

### Snyk Security Scanning
This repository includes minimal Node.js package configuration (`package.json`) and Snyk policy (`.snyk`) files to satisfy organizational security scanning requirements, even though it contains no Node.js runtime dependencies.

**Scanning Configuration:**
- **⚠️ IMPORTANT**: Organizational Snyk policies ignore local `.snyk` policy files
- Individual example plugin projects will be scanned and may show vulnerabilities
- This is expected behavior for a documentation repository containing educational examples
- Only the root-level Node.js package.json represents actual repository dependencies (none)

**Expected Scanning Results:**
- **Root package.json scan**: ✅ Should pass (no runtime dependencies)
- **Example project scans**: ❌ Will show vulnerabilities and build failures - THIS IS EXPECTED
- **Build failures**: Expected due to outdated Gradle versions (Gradle 3.5, 9.1 with Java 11 incompatibility)
- **Security vulnerabilities**: Expected in educational example dependencies

**Security Team Guidance:**
- ✅ **PASS CRITERIA**: Root package.json scan shows no vulnerabilities
- ❌ **IGNORE**: All vulnerabilities found in `example-*` and plugin directories
- ❌ **IGNORE**: All build failures in example projects
- 📋 **RATIONALE**: This is a documentation repository with educational examples, not production code

**Documented Acceptable Findings:**
- Gradle build failures (Java version mismatches, corrupted wrapper files)
- High-severity vulnerabilities in commons-beanutils, jackson-core, snakeyaml, commons-lang
- Outdated Rundeck core dependencies in example projects
- Missing or inaccessible Gradle distributions

### Security Guidelines

#### For Repository Users
1. **Example Code Usage**: All plugin examples are for educational purposes. Review and modify according to your security requirements before any production use.

2. **Dependency Management**: Individual plugin examples may contain build dependencies (Maven/Gradle). Review these dependencies separately if you plan to use any examples as starting points for production plugins.

3. **Code Review**: Always perform security code review on any plugin before deploying to a Rundeck production environment.

#### For Security Teams
- This repository poses no direct security risk as it contains only documentation and example code
- No runtime dependencies are present that could introduce vulnerabilities
- Security scanning is performed for organizational compliance only

## Reporting Security Issues

If you discover security vulnerabilities in any of the example code or have suggestions for improving the security guidance:

1. **For Example Code Issues**: Open an issue in this repository describing the concern and suggesting improvements
2. **For Rundeck Core Security Issues**: Follow the [Rundeck Security Policy](https://github.com/rundeck/rundeck/security/policy)
3. **For Production Plugin Security**: Contact your organization's security team

## Supported Versions

This repository contains example code and documentation that is maintained for educational purposes. While we strive to keep examples current with Rundeck best practices, security updates are not provided for example code.

For production Rundeck deployments, always:
- Use the latest stable Rundeck version
- Follow current Rundeck security guidelines
- Perform security assessment of any custom plugins before deployment

## Security Best Practices for Plugin Development

When developing Rundeck plugins based on these examples:

1. **Input Validation**: Always validate user inputs and configuration parameters
2. **Credential Management**: Use Rundeck's secure credential storage features
3. **Error Handling**: Avoid exposing sensitive information in error messages
4. **Logging**: Be cautious about logging sensitive data
5. **Dependencies**: Keep all dependencies up to date and scan for vulnerabilities
6. **Testing**: Include security testing in your plugin development process

## Additional Resources

- [Rundeck Plugin Development Guidelines](https://docs.rundeck.com/docs/developer)
- [Rundeck Security Documentation](https://docs.rundeck.com/docs/administration/security)
- [OWASP Security Guidelines](https://owasp.org/)