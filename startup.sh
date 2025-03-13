#docker默认使用root创建挂载目录，但容器内用户为1001,写入文件会没有权限
#所以需要提前创建，并开放权限
#post_start由于触发时机也不能提前修改文件

#这里使用1001用户执行
mkdir -p \
  /repo/backend /data/backend \
  /data/mysql/data /data/mysql/logs \
  /data/es/data \
  /data/redis/data \
  /data/rabbitmq/data /data/rabbitmq/logs \
  /data/nginx/logs

cd docker
docker compose up -d --build