#!/usr/bin/env sh
cd "$(dirname "$0")"
exec java -Xmx6G -jar spicedcider-server.jar "$@"
