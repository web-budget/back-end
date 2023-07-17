ARG JAVA_VERSION

FROM eclipse-temurin:${JAVA_VERSION}-jdk AS jdk
RUN mkdir -p /build
WORKDIR /build

FROM jdk AS jar-extractor

ARG JAVA_VERSION

ARG ARTIFACT_PATH
RUN ["/bin/sh", "-c", ": ${ARTIFACT_PATH:?Artifact path not set or empty}"]

COPY entrypoint.sh entrypoint.sh
COPY $ARTIFACT_PATH app.jar

RUN java -Djarmode=layertools -jar app.jar extract

RUN jdeps \
    --multi-release ${JAVA_VERSION} \
    --class-path 'spring-boot-loader/org/*:snapshot-dependencies/BOOT-INF/lib/*:dependencies/BOOT-INF/lib/*' \
    --recursive \
    --ignore-missing-deps \
    --print-module-deps \
    -quiet \
    app.jar \
    > java-modules.txt

RUN sed -i ' 1 s/.*/&,jdk.crypto.ec,jdk.management/' java-modules.txt

FROM jdk AS jre-builder

COPY --from=jar-extractor /build/java-modules.txt .

RUN jlink \
    --verbose \
    --add-modules $(cat java-modules.txt) \
    --compress 2 \
    --strip-java-debug-attributes \
    --no-header-files \
    --no-man-pages \
    --output "/build/opt/java-minimal"

FROM debian:stable-slim AS package

MAINTAINER arthurshakal@gmail.com

ENV JAVA_HOME="/opt/java-minimal"
ENV PATH="$PATH:$JAVA_HOME/bin"

EXPOSE 8085

RUN mkdir -p /workspace/lib
RUN mkdir -p /workspace/log

VOLUME ["/workspace/log"]

WORKDIR /workspace/lib

RUN rm -rf /var/lib/apt/lists/*

COPY --from=jar-extractor /build/entrypoint.sh /workspace
COPY --from=jre-builder "/build/opt/java-minimal" "$JAVA_HOME"

COPY --from=jar-extractor /build/dependencies/ /workspace/lib
COPY --from=jar-extractor /build/spring-boot-loader/ /workspace/lib
COPY --from=jar-extractor /build/snapshot-dependencies/ /workspace/lib
COPY --from=jar-extractor /build/application/ /workspace/lib

ENTRYPOINT ["sh", "/workspace/entrypoint.sh"]
