FROM python:3-slim
WORKDIR /app
COPY ./requirements.txt ./requirements.txt
RUN python3 -m pip install --disable-pip-version-check -r requirements.txt
COPY ./scripts ./scripts
ENTRYPOINT ["exit", "1"]
