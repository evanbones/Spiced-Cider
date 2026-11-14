#!/usr/bin/env sh
set -e
cd "$(dirname "$0")"
if [ ! -f run.sh ]; then
  java -jar neoforge-installer.jar --installServer
  rm -f neoforge-installer.jar.log
fi
if ! grep -q '^-Xmx' user_jvm_args.txt 2>/dev/null; then
  printf '\n-Xmx6G\n' >> user_jvm_args.txt
fi
sh run.sh nogui "$@"
