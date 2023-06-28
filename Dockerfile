FROM eclipse-temurin:19
VOLUME /dicegame
COPY build/libs/S05T02MZNMON-0.0.1-SNAPSHOT.jar dicegame.jar
ENTRYPOINT ["java","-jar","/dicegame.jar"]
