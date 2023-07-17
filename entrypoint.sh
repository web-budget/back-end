#!/bin/bash

set -e

exec java \
-XX:InitialRAMPercentage=80 \
-XX:MaxRAMPercentage=80 \
-XX:+UseG1GC \
-XX:+UseStringDeduplication \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/workspace/log \
-Dfile.encoding=UTF-8 \
-Duser.timezone=America/Sao_Paulo \
-Dspring.profiles.active=prod \
"org.springframework.boot.loader.JarLauncher" \
"$@"
