#!/bin/bash
current_time=$(date +"%Y-%m-%d_%H-%M-%S")
echo "当前系统时间为 $current_time"
echo "清除超过15天的备份文件"
find /data/ -maxdepth 1 -name "mikufans-*.gz" -mtime +15 -delete
echo "开始创建备份文件"
tar -zcvf "/backup/mikufans-$current_time.gz" /backup/
echo "创建备份完成"