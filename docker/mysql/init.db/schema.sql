drop database if exists mikufans;

create database mikufans
    default character set utf8mb4
    default collate utf8mb4_0900_ai_ci;

drop user if exists 'miku'@'%';

create user 'miku'@'%' identified by '123456';

grant all on mikufans.* to 'miku'@'%';

#禁止root远程登录
#update mysql.user set account_locked = 'Y' where Host = '%' and User = 'root';
update mysql.user set Host = 'localhost' where Host = '%' and User = 'root';

flush privileges;