FROM python:3.9.1-slim
WORKDIR /app
COPY ./requirements.txt ./requirements.txt
RUN pip3 install --disable-pip-version-check -r requirements.txt
COPY ./backend_server.py ./backend_server.py
ENV FLASK_APP=backend_server
ENV FLASK_ENV=development
EXPOSE 5000
CMD ["python3", "-m", "flask", "run", "--host=0.0.0.0"]
