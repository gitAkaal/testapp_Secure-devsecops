# Security Scanning Workflow Documentation

This document explains the GitHub Actions workflow defined in `security-scan.yml` which performs comprehensive security scanning including SAST (Static Application Security Testing) and DAST (Dynamic Application Security Testing).

## Overview

The workflow provides end-to-end security scanning capabilities:
1. Static Analysis (SAST)
   - Python code scanning with Bandit
   - Dependency vulnerability checking with OWASP Dependency Check
   - Container and filesystem scanning with Trivy
2. Dynamic Analysis (DAST)
   - Web application scanning with OWASP ZAP
3. Container Security
   - Image vulnerability scanning
   - Configuration assessment
   - SBOM generation

## Workflow Triggers

```yaml
on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  workflow_dispatch:  # Manual trigger
```

The workflow runs on:
- Any push to the main branch
- Any pull request targeting main branch
- Manual trigger via GitHub Actions UI

- Automatically triggers on pushes to `main` branch when relevant files change
- Runs on pull requests targeting `main` branch
- Can be manually triggered via GitHub Actions UI

### Manual Trigger Options

When triggering the workflow manually, you can customize the scan with these parameters:

1. **scan_type** (required):
   - `all`: Run both SAST and DAST scans
   - `sast`: Run only static analysis
   - `dast`: Run only dynamic analysis

2. **severity_level** (optional):
   - `HIGH,CRITICAL` (default): Only high and critical issues
   - `CRITICAL`: Only critical issues
   - `HIGH`: Only high severity issues
   - `MEDIUM,HIGH,CRITICAL`: Medium and above
   - `LOW,MEDIUM,HIGH,CRITICAL`: All severity levels

3. **target_path** (optional):
   - Specific path to scan
   - Defaults to entire repository ('.')

## Jobs and Their Functions

### 1. Get Changed Files
- Tracks file changes in Python and Java code
- Identifies Docker-related changes
- Helps optimize scanning by focusing on changed components

### 2. Bandit SAST Scan
- Python-specific security scanner
- Detects common security issues
- Generates detailed JSON reports

### 3. OWASP Dependency Check
- Scans Java dependencies using Maven
- Analyzes Python requirements with pip-audit
- Identifies known vulnerabilities (CVEs)

### 4. Trivy Filesystem Scan
- Multi-scanner approach:
  - Vulnerability scanning
  - Secret detection
  - Misconfiguration checks
- Language-aware scanning for Python and Java

### 5. Docker Build & Security
- Multi-stage Docker builds
- Secure build practices
- Container registry integration
- SBOM generation for Java builds

### 6. Trivy Container Scan
- Container vulnerability scanning
- Configuration assessment
- Customizable severity thresholds
- Detailed reporting

### 7. ZAP DAST Scan
- Dynamic application security testing
- Automated vulnerability discovery
- Custom alert filtering
- Multiple report formats (JSON, XML, HTML)

## Artifacts and Reports

Each job generates detailed reports and artifacts:

1. **Bandit Results**
   - `bandit-report.txt`: Human-readable summary
   - `bandit-results.json`: Detailed findings

2. **Dependency Check Results**
   - `dependency-check-report.html`: Interactive report
   - `dependency-check-report.json`: Machine-readable data
   - `pip-audit-results.json`: Python dependency scan
   - `pip-audit-report.txt`: Formatted Python findings

3. **Trivy Scan Results**
   - Filesystem scan reports
   - Container vulnerability reports
   - Configuration assessment findings

4. **ZAP Scan Results**
   - `zap-report.json`: Complete findings
   - `zap-report.xml`: XML format
   - `zap-report.html`: Interactive report
   - `zap-summary.txt`: Quick overview

## Environment Variables

The workflow uses these key environment variables:
```yaml
env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}
  python_changes: Files with .py extension
  java_changes: Files with .java extension
  dockerfile_changed: Changes in Dockerfile or requirements.txt
```
- Uses Git diff to identify changed files
- Categorizes changes by file type
- Outputs are used by subsequent jobs to determine what to scan

### 2. Trivy Filesystem Scan (`trivy-fs-scan`)
- Runs when:
  - Not manually triggered, or
  - Manually triggered with `scan_type` = `all` or `sast`
- Scans for:
  - Vulnerabilities
  - Secrets
  - Misconfigurations
- Generates separate reports for Python and Java code
- Uses configured severity levels

### 3. Docker Build and Push (`docker-build-push`)
- Runs after filesystem scan
- Triggers when:
  - Python files change
  - Java files change
  - Dockerfile changes
- Builds and pushes to GitHub Container Registry
- Uses buildx for efficient caching

### 4. Trivy Container Image Scan (`trivy-image-scan`)
- Scans the built container image
- Checks for:
  - OS vulnerabilities
  - Package vulnerabilities
  - Container misconfigurations
- Generates JSON reports
- Respects configured severity levels

### 5. ZAP DAST Scan (`zap-scan`)
- Runs when:
  - Not manually triggered, or
  - Manually triggered with `scan_type` = `all` or `dast`
- Performs dynamic security testing
- Features:
  - Alert filtering for common false positives
  - Full scan of running application
  - Generates HTML, XML, and JSON reports

## Artifacts and Reports

Each scanning job generates and uploads artifacts:

1. **Trivy Filesystem Scan**:
   - `trivy-fs-report.txt`
   - `python-scan.json`
   - `java-scan.json`

2. **Trivy Container Scan**:
   - `container-scan-report.txt`
   - `vuln-scan.json`
   - `config-scan.json`

3. **ZAP Scan**:
   - `zap-summary.txt`
   - `zap-report.json`
   - `zap-report.xml`
   - `zap-report.html`

All artifacts are retained for 90 days except ZAP results (1 day).

## Environment Variables

```yaml
env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}
```
- `REGISTRY`: GitHub Container Registry URL
- `IMAGE_NAME`: Automatically set to repository name

## Permissions

The workflow requires specific permissions:
- `contents: read` - For checking out code
- `packages: write` - For pushing to container registry
- `security-events: write` - For security scanning results

## Example Usage

1. **Automatic Scanning**:
   - Push changes to `main` branch
   - Create a pull request

2. **Manual Scanning**:
   - Go to Actions tab
   - Select "Security Scan (SAST & DAST)"
   - Click "Run workflow"
   - Configure options:
     ```
     Scan Type: all
     Severity Level: HIGH,CRITICAL
     Target Path: .
     ```

## Best Practices

1. **Regular Scanning**:
   - Run scans on all pull requests
   - Regular scans on main branch

2. **Severity Levels**:
   - Start with HIGH,CRITICAL
   - Gradually include MEDIUM as codebase matures
   - Use LOW for comprehensive audits

3. **Reports Review**:
   - Monitor scan results regularly
   - Address high-severity issues promptly
   - Track false positives with ZAP hooks
