# Security Testing Application

This project contains intentionally vulnerable applications written in Java and Python for security testing purposes. It includes automated security scanning using various SAST (Static Application Security Testing) and DAST (Dynamic Application Security Testing) tools.

⚠️ **WARNING**: These applications contain intentional security vulnerabilities. DO NOT deploy in production environments.

## Project Structure

```
├── java_wordstop/              # Java vulnerable application
│   └── src/main/java/com/wordstop/
│       ├── WordStop.java       # Main vulnerable Java application
│       └── URLFetcher.java     # SSRF vulnerable component
└── python_wordstop/           # Python vulnerable application
    ├── wordstop.py           # Main Flask application with vulnerabilities
    └── test_wordstop.py      # Test file
```

## Security Vulnerabilities

The applications demonstrate various OWASP Top 10 vulnerabilities including:
- Server-Side Request Forgery (SSRF)
- SQL Injection
- Command Injection
- And more...

## Automated Security Scanning

This project uses GitHub Actions to automatically run security scans on code changes:

### SAST Tools
- **Bandit**: Python-specific security scanner for detecting common security issues
- **OWASP Dependency Check**: Identifies known vulnerabilities in project dependencies
- **Trivy**: Multi-purpose security scanner for:
  - File system scanning
  - Container image vulnerabilities
  - Infrastructure as Code (IaC) issues
  - Secret detection

### DAST Tools
- **OWASP ZAP**: Performs dynamic security testing on running applications
  - Automated vulnerability scanning
  - API security testing
  - Configurable alert filtering

### Container Security
- Docker image scanning with Trivy
- Software Bill of Materials (SBOM) generation
- Container configuration assessment
- Multi-stage build security

## Workflow Features

- Runs automatically on push to main and pull requests
- Manual trigger support with customizable options:
  - Scan type selection (SAST/DAST/All)
  - Severity level filtering
  - Target path specification
- Intelligent file change detection
- Parallel execution of security tools
- Comprehensive scan reports and artifacts
- GitHub Container Registry integration
- Secure variable handling and sanitization

## Usage

1. For local development:
   ```bash
   # Python application
   cd python_wordstop
   pip install flask
   python -m flask run

   # Java application
   cd java_wordstop
   ./mvnw spring-boot:run   # If using Spring Boot
   ```

2. View security scan results:
   - Go to GitHub Actions tab
   - Select the latest workflow run
   - Download scan artifacts for detailed reports

## Security Reports

Security scan reports are saved as artifacts for each commit and pull request. You can find them:
1. In the GitHub Actions workflow run
2. Under the "Artifacts" section of each run
3. Reports are organized by tool (Bearer, Bandit, Trivy, ZAP)

## Note

This is a testing application designed to demonstrate security vulnerabilities. It should only be used in controlled testing environments.
