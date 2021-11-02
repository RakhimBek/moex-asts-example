clear

./gradlew clean
./gradlew jar

docker build -t ubee .
docker rm ubee
docker run --name ubee -p 56319:56319 -p 59623:59623 -p 16411:16411 -p 16412:16412 -p 26411:26411 -p 26412:26412 -v "$(pwd)"/log:/log -it ubee
