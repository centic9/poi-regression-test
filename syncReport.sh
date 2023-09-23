#!/bin/bash

set -e

# change to something else and set up public/private key auth
export DESTINATION=user@home.apache.org:/home/user

echo "Unmounting"
fusermount -u ../transfer 2> /dev/null || true

rmdir ../transfer 2> /dev/null || true

mkdir -p ../transfer

echo "Mounting"
# -o sshfs_debug 
sshfs -o idmap=user -o gid=1000 -o nomap=error -o HostKeyAlgorithms=ssh-rsa ${DESTINATION} ../transfer

mkdir -p ../transfer/public_html/poi_regression

# removed --delete to not delete/copy documents again and again

# html-files first to be able to publish the results earlier
echo "Syncing html files"
rsync --verbose --progress --archive --chmod=a+r build/reports/*.html ../transfer/public_html/poi_regression/reports/

echo "Syncing html-all files"
rsync --verbose --progress --archive --chmod=a+r build/reportsAll/*.html ../transfer/public_html/poi_regression/reportsAll/

# then all documents
echo "Syncing all files"
rsync --verbose --progress --archive --checksum --chmod=a+r build/reports build/reportsAll ../transfer/public_html/poi_regression/

echo "Done"
sleep 5

fusermount -u ../transfer

rmdir ../transfer
