clear

docker build -t ubee .
docker run -p 56319:56319 -p 59623:59623 -p 16411:16411 -p 16412:16412 -p 26411:26411 -p 26412:26412 -v :/opt/app/ -it ubee