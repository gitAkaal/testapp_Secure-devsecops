from flask import Flask, request
import subprocess
import os

app = Flask(__name__)

@app.route('/run_command', methods=['POST'])
def run_command():
    """
    WARNING: This endpoint is intentionally vulnerable to OS command injection
    DO NOT USE IN PRODUCTION
    """
    command = request.form.get('command', '')
    
    # Vulnerable: Direct use of user input in system command
    try:
        output = os.popen(command).read()
        return f"Command output: {output}"
    except Exception as e:
        return f"Error executing command: {str(e)}"

@app.route('/system_info', methods=['GET'])
def system_info():
    """
    WARNING: This endpoint is intentionally vulnerable to OS command injection
    DO NOT USE IN PRODUCTION
    """
    # Vulnerable: Unsanitized user input used in command
    host = request.args.get('host', 'localhost')
    
    # Vulnerable: Command injection possible through host parameter
    try:
        result = subprocess.check_output(f"ping {host}", shell=True)
        return result.decode('utf-8')
    except subprocess.CalledProcessError as e:
        return f"Error: {str(e)}"

if __name__ == '__main__':
    app.run(debug=True)
