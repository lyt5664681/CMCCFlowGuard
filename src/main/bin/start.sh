#!/bin/bash
#设置环境变量
. /etc/profile
. ~/.bash_profile

WORK_ROOT=$(cd "$(dirname "$0")"; pwd)
cd $WORK_ROOT
echo $WORK_ROOT

JAVA_OPTS="$JAVA_OPTS -Drun_dir=$WORK_ROOT"
JAVA_OPTS="$JAVA_OPTS -Xss256k -Xms1024m -Xmx1024m -Xss512k"
JAVA_OPTS="$JAVA_OPTS -XX:PermSize=256m -XX:MaxPermSize=256m"
JAVA_OPTS="$JAVA_OPTS -Dlog4j2.formatMsgNoLookups=true"

echo $JAVA_OPTS

nohup java $JAVA_OPTS -jar ../lib/CMCCFlowGuard-1.3.2.jar $@ >run.log 2>&1 &