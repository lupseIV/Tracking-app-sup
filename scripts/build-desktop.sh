#!/usr/bin/env bash
# Builds the desktop app: Angular production build is served by the Spring
# Boot jar (copied into its static resources), and Electron wraps the jar.
set -euo pipefail
cd "$(dirname "$0")/.."

echo "==> Building Angular frontend"
(cd frontend && npx ng build)

echo "==> Copying frontend build into backend static resources"
rm -rf backend/src/main/resources/static
mkdir -p backend/src/main/resources/static
cp -r frontend/dist/frontend/browser/* backend/src/main/resources/static/

echo "==> Building backend jar"
(cd backend && mvn -q package -DskipTests)

echo "==> (Optional) Bundle a trimmed JRE for distribution"
if [ ! -d desktop/jre ]; then
  echo "    desktop/jre not found - creating one with jlink"
  jlink --add-modules java.base,java.desktop,java.instrument,java.management,java.naming,java.net.http,java.prefs,java.scripting,java.security.jgss,java.sql,jdk.unsupported \
        --strip-debug --no-header-files --no-man-pages --compress=zip-6 \
        --output desktop/jre
fi

echo "==> Packaging with electron-builder"
(cd desktop && npm install && npm run dist)

echo "Done. Installers are in desktop/dist/"
