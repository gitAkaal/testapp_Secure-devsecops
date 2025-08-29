# Security Scanning Workflow Documentation

This document explains the GitHub Actions workflow defined in `security-scan.yml` which performs comprehensive security scanning including SAST (Static Application Security Testing) and DAST (Dynamic Application Security Testing).

## Workflow Triggers

```yaml
on:
  push:
    branches: [ main ]
    paths:
      - '**.py'    # Python files
      - '**.java'  # Java files
      - 'Dockerfile'
      - '**/requirements.txt'
      - 'pom.xml'
  pull_request:
    branches: [ main ]
  workflow_dispatch:  # Manual trigger
```

- Automatically triggers on pushes to `main` branch when relevant files change
- Runs on pull requests targeting `main` branch
- Can be manually triggered via GitHub Actions UI

### Manual Trigger Options

The workflow supports the following input parameters when triggered manually:

1. **scan_type** (required):
   - `all`: Run both SAST and DAST scans
   - `sast`: Run only static analysis
   - `dast`: Run only dynamic analysis

2. **severity_level** (optional):
   - `HIGH,CRITICAL` (default)
   - `CRITICAL`
   - `HIGH`
   - `MEDIUM,HIGH,CRITICAL`
   - `LOW,MEDIUM,HIGH,CRITICAL`

3. **target_path** (optional):
   - Specific path to scan
   - Defaults to entire repository

## Jobs Description

### 1. Get Changed Files (`get-changed-files`)
```yaml
outputs:
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
