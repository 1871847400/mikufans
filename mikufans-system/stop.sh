pid=`head -20 server.log | grep PID | awk '{print $21}'`
echo KIll PID=$pid
kill -9 $pid
