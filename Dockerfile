FROM python:3.12-slim

WORKDIR /app

COPY python_wordstop/requirements.txt .
RUN pip install -r requirements.txt
RUN pip install pandas

COPY python_wordstop/ .

EXPOSE 5000

CMD ["python", "-m", "flask", "run", "--host=0.0.0.0"]
