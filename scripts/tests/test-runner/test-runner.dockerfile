FROM python:3-slim
WORKDIR /app
COPY ./requirements.txt ./requirements.txt
RUN pip3 install --disable-pip-version-check -r requirements.txt
COPY ./scripts ./scripts
ENTRYPOINT ["exit", "1"]
