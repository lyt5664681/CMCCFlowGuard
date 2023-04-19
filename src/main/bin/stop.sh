#!/bin/sh

# kill all api server process
ps -ef |  grep CMCCFlowGuard | grep -v grep | awk '{print $2}' | xargs -I {} kill -9  {}

exit 0
