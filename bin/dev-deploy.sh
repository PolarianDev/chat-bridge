#!/bin/bash
# Permission to use, copy, modify, and/or distribute this software for
# any purpose with or without fee is hereby granted.
#
# THE SOFTWARE IS PROVIDED “AS IS” AND THE AUTHOR DISCLAIMS ALL
# WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES
# OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE
# FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY
# DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN
# AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT
# OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

# Script to deploy plugin to a remote SFTP server.
# deploy.conf should contain the following key value pairs
# USER=foo
# HOST=example.com
# PORT=3333

PATH="plugins"  # Path to the plugins directory on the remote
CONFIG="deploy.conf"  # Path to the config file, this script should be executed within the root of the project
PASS_FILE=".sftp_password"  # path to the password file for sshpass
CONFIG_VALUES="USER HOST PORT"  # Values which must exist within the config file
PLUGIN_NAME="chat-bridge"  # Name of the plugin, used to remove the old plugin by checking for PLUGIN_NAME-*
PLUGIN_PATH="target/chat-bridge"  # Path to the compiled plugin, -* will be appended to the end when deploying

# Ensure sshpass and maven are installed
[ ! -f "/usr/bin/sshpass" ] && echo "Please install sshpass to use this script" && exit 1
[ ! -f "/usr/bin/mvn" ] && echo "Please install maven to use this script" && exit 1

# Build project before attempting to deploy it
PATH="/usr/bin" /usr/bin/mvn clean package || exit 1

# Check if the config exists, and is not empty
[ ! -s $CONFIG ] && echo "Could not find config or config is empty" && exit 1

# Source the config
. $CONFIG

# Ensure all variables are set from the config
for value in $CONFIG_VALUES
do
  if [ ! -v $value ]; then echo "$value is not set" && exit 1; fi;
done

# Ensure sftp password file exists and has content
[ ! -s $PASS_FILE ] && echo "Could not find sftp password" && exit 1

# Connect to remote sftp server and remove the old plugin and copy over the new plugin
/usr/bin/sshpass -f $PASS_FILE /usr/bin/sftp -P $PORT "$USER@$HOST:$PATH" << EOF
rm $PLUGIN_NAME-*
put $PLUGIN_PATH-*
exit
EOF
