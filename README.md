# Rundeck Plugin Examples

This repository serves as a comprehensive knowledge base for developing Rundeck plugins. It includes a variety of example plugins, each demonstrating best practices and adhering to the latest [Plugin Development Guidelines](https://pagerduty.atlassian.net/wiki/spaces/RUNDECK/pages/2878472445/Workflow+Step+Node+Step+Plugins+Development+Guidelines).

These examples are designed to help you understand, edit, and build plugins for testing purposes.

---

## Included Plugins

The repository contains the following example plugins:

### Groovy Plugins
- **[Example Step Plugins](example-groovy-step-plugin/README.md)**: Demonstrates step plugins written in Groovy, including workflow and node step plugins.
- **[Example Notification Plugins](example-groovy-notification-plugins/Readme.md)**: Includes examples for sending notifications, such as email and shell script-based notifications.
- **[Example Log Plugins](example-groovy-log-plugins/)**: Provides examples for custom log handling.

### Java Plugins
- **[Example Step Plugins](example-java-step-plugin/README.md)**: Demonstrates step plugins written in Java, including node step and remote script node step plugins.
- **[Example Notification Plugin](example-java-notification-plugin/)**: An example notification plugin written in Java.
- **[Example Audit Plugin](example-java-audit-plugin/README.md)**: Captures auditing events from the Rundeck application.
- **[Example Execution Lifecycle Plugin](example-java-execution-lifecyle-plugin/)**: Demonstrates execution lifecycle hooks for Rundeck jobs.
- **[Example Storage Plugins](example-java-storage-plugin/)**: Examples for custom storage backends.

### Script-Based Plugins
- **[Example Script Node Step Plugin](example-script-node-step-plugin/README.md)**: A script-based node step plugin.
- **[Example Script Remote Node Step Plugin](example-script-remote-node-step-plugin/README.md)**: A script-based remote node step plugin.

### JSON Resource Format Plugin
- **[JSON Plugin](json-plugin/README.md)**: Provides JSON format support for resource definitions, including a parser and generator.

---

## How to Use

### Building Plugins
Each plugin includes build instructions in its respective `README.md`. Most plugins use Gradle or Maven for building. For example:
```sh
./gradlew build
```

The resulting `.jar` or `.zip` file can be found in the build/libs directory.

### Installing Plugins

To install a plugin:

1. Copy the built plugin file (e.g., .jar or .zip) to the $RDECK_BASE/libext directory of your Rundeck server.
2. Alternatively, upload the plugin via the Rundeck GUI under Plugins > Upload Plugin.

### Testing Plugins

Refer to the individual plugin README.md files for usage instructions and configuration details.

## Repository Classification

**Repository Type:** Configuration-only documentation repository  
**Security Posture:** Low risk - contains only example code and documentation  
**Runtime Dependencies:** None

This repository satisfies organizational security scanning requirements through minimal compliance files (`package.json`, `.snyk`, `SECURITY.md`) while accurately representing its nature as a documentation and examples repository. The repository contains no production runtime dependencies or executable Node.js code.

### Security Scanning
- **Snyk Scanning:** Configured for compliance with organizational security policies
- **Scanning Scope:** Only the root repository is scanned; individual example projects are excluded via `.snyk` and `.snykignore` files
- **Build Issues:** Example projects may have build failures due to outdated Gradle versions - this is expected for educational examples
- **Vulnerability Management:** Known vulnerabilities in example dependencies are documented and ignored as they relate to non-production code
- **Security Guidance:** See [SECURITY.md](SECURITY.md) for detailed security information

⚠️ **Important:** Example plugins are for educational purposes only. Always perform security review and testing before deploying any plugin to a production Rundeck environment.

## Contributing

We welcome contributions to improve these examples or add new ones. Please follow these steps:

1. Fork the repository.
2. Create a feature branch.
3. Submit a pull request with a detailed description of your changes.


## A Note on Historical Examples

Historically, example plugin code was located in the examples folder of the Rundeck project. This repository aims to modernize and align those examples with the latest guidelines. Check back regularly for updates.

## Resources

- [Rundeck Plugin Development Guides](https://docs.rundeck.com/docs/developer)
- [Rundeck GitHub Repository](https://github.com/rundeck/rundeck)
- [Plugin Bootstrap Tool Info](https://docs.rundeck.com/docs/learning/howto/plugin-bootstrap.html)

## License

This repository is licensed under the Apache License 2.0.