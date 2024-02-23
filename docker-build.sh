#!/usr/bin/env bash

set -x

VERSION=$(git describe --tags)

echo "Building version: ${VERSION}"

./gradlew bootJar

DOCKER_IMAGE_NAME=fluidtokens/fluidtokens-bot
DOCKER_IMAGE="${DOCKER_IMAGE_NAME}:${VERSION}"
DOCKER_IMAGE_LATEST="${DOCKER_IMAGE_NAME}:latest"

docker build -t "${DOCKER_IMAGE}" -t "${DOCKER_IMAGE_LATEST}" .
