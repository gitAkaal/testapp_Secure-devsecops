from flask import Flask, request, jsonify, session
import sqlite3
import hashlib
import os
import subprocess
import base64

app = Flask(__name__)
app.secret_key = "super_secret_key"  # Vulnerability: Hardcoded Secret Key

# Vulnerability: Insecure Database Connection
def get_db_connection():
    return sqlite3.connect('database.db')

# Vulnerability: SQL Injection
@app.route('/login', methods=['POST'])
def login():
    username = request.form.get('username')
    password = request.form.get('password')
    
    conn = get_db_connection()
    cursor = conn.cursor()
    
    # Vulnerable SQL query
    query = f"SELECT * FROM users WHERE username = '{username}' AND password = '{password}'"
    cursor.execute(query)
    user = cursor.fetchone()
    
    if user:
        session['user_id'] = user[0]
        return jsonify({"status": "success"})
    return jsonify({"status": "failed"})

# Vulnerability: Command Injection
@app.route('/ping', methods=['POST'])
def ping_host():
    hostname = request.form.get('hostname')
    # Vulnerable command execution
    result = subprocess.check_output(f"ping {hostname}", shell=True)
    return jsonify({"output": result.decode()})

# Vulnerability: Weak Cryptography
@app.route('/encrypt', methods=['POST'])
def encrypt_data():
    data = request.form.get('data')
    # Using weak MD5 hash
    encrypted = hashlib.md5(data.encode()).hexdigest()
    return jsonify({"encrypted": encrypted})

# Vulnerability: Insecure Direct Object References
@app.route('/user/<user_id>/profile', methods=['GET'])
def get_user_profile(user_id):
    conn = get_db_connection()
    cursor = conn.cursor()
    # No authorization check
    cursor.execute(f"SELECT * FROM users WHERE id = {user_id}")
    user = cursor.fetchone()
    return jsonify({"user": user})

# Vulnerability: Security Misconfiguration
@app.route('/debug')
def debug_info():
    # Exposing sensitive information
    debug_info = {
        "app_secret": app.secret_key,
        "db_connection": str(get_db_connection()),
        "environment": os.environ,
        "python_version": sys.version
    }
    return jsonify(debug_info)

if __name__ == '__main__':
    # Vulnerability: Security Misconfiguration
    app.run(debug=True, host='0.0.0.0')
