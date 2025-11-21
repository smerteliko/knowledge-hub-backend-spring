FROM ubuntu:latest
LABEL authors="smerteliko"

ENTRYPOINT ["top", "-b"]