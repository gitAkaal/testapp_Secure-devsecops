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
- **Bearer**: Scans Java files for security issues
- **Bandit**: Analyzes Python code for security vulnerabilities
- **Trivy**: Performs comprehensive vulnerability scanning

### DAST Tools
- **OWASP ZAP**: Performs dynamic security testing on the running application

## Workflow Features

- Runs automatically on push to main and pull requests
- Scans only changed files for efficiency
- Parallel execution of security tools
- Detailed scan reports for each tool

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
