#!/usr/bin/env bash
set -e
source /etc/profile
SELF_DIR="$(cd "$(dirname "$0")" && pwd)"
cd ${SELF_DIR}

STORE_DIR=./gen

./gradlew clean build

module=comparator
distDir=./${module}/build/distributions
builtZip=${distDir}/${module}.zip

timestamp=$(date "+%Y-%m-%d_%H-%M-%S_%N")
commitHash=$(git rev-parse --short HEAD)
if [ -n "${STORE_DIR}" ]; then
    storeDir=${STORE_DIR}
else
    storeDir=./gen
fi
storeFile=${storeDir}/${module}_${commitHash}_${timestamp}.zip
mkdir -p ${storeDir}
cp ${builtZip} ${storeFile}

latestDir=${storeDir}/latest
latestBuilt=${latestDir}/${module}_${commitHash}_${timestamp}.zip
rm -r -f ${latestDir}
mkdir -p ${latestDir}
cp ${builtZip} ${latestBuilt}

echo The latest built is ${latestBuilt}