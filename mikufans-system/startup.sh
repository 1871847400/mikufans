JAR_NAME=mikufans-core-1.0.0.jar
TARGET=mikufans-core/target/$JAR_NAME

pid=`ps -ef | grep $JAR_NAME | grep 'java -jar' | awk '{print $2}'`
if [ ${pid} ]; then
  echo 'Stopping exist process...'
  kill -15 $pid
  sleep 2
  pid=`ps -ef | grep $JAR_NAME | grep 'java -jar' | awk '{print $2}'`
  if [ ${pid} ]; then
    echo 'Stopping failed!'
  else
    echo 'Stopping successed!'
  fi
fi
ulimit -n 65536
mvn clean package -Dmaven.test.skip=true
#nohup java -jar -Dspring.profiles.active=def,prod $JAR_NAME &> server.log &
java -jar -Xms512M -Xmx1024M -XX:+UseG1GC -XX:+UseCompressedOops -Dspring.profiles.active=def,prod $TARGET