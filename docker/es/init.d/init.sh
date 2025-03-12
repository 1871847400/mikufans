(
  sleep 30 && 
  /opt/bitnami/elasticsearch/bin/elasticsearch-setup-passwords interactive < /docker-entrypoint-initdb.d/input.txt && 
  echo 已成功为所有用户设置密码
) &