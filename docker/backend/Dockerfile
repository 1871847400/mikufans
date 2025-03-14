#继承centos7
FROM centos:7

#设置工作目录
WORKDIR /app

#说明
#RUN在build过程中的执行命令，ENTRYPOINT和CMD在run后执行

#修改yum镜像源,默认的可能无法使用
RUN curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo

#安装第三方仓库(包含最新的一些库)
#RUN curl https://setup.ius.io | sh
#RUN yum install -y https://packages.endpointdev.com/rhel/7/os/x86_64/endpoint-repo.x86_64.rpm

#更新yum仓库和清除缓存
RUN yum -y update && yum clean all

#安装基础工具
RUN yum install -y vim wget tree net-tools

#安装中文环境和字体
RUN yum install -y glibc-common zh-CN wqy-microhei-fonts

#设置语言环境为中文
ENV LANG zh_CN.UTF-8
ENV LANGUAGE zh_CN:zh
ENV LC_ALL zh_CN.UTF-8

#安装python3和pip3
#RUN yum install -y python3
#RUN pip3 config set global.index-url https://mirrors.aliyun.com/pypi/simple/
#RUN pip3 config set global.target /data/pip-packages

#安装nodejs
ADD node-v22.13.1-linux-x64-glibc-217.tar.gz /usr/local/nodejs
#创建软连接
RUN ln -s /usr/local/nodejs/node-v22.13.1-linux-x64-glibc-217/bin/node /usr/bin/node
RUN ln -s /usr/local/nodejs/node-v22.13.1-linux-x64-glibc-217/bin/npm /usr/bin/npm
RUN ln -s /usr/local/nodejs/node-v22.13.1-linux-x64-glibc-217/bin/npx /usr/bin/npx
#安装yarn
#RUN npm install -g yarn
#RUN ln -s /usr/local/nodejs/node-v22.13.1-linux-x64-glibc-217/bin/yarn /usr/bin/yarn

#安装jdk
RUN rpm -ivh https://repo.huaweicloud.com/java/jdk/8u202-b08/jdk-8u202-linux-x64.rpm
#COPY jdk-8u202-linux-x64.rpm /tmp/
#RUN rpm -ivh /tmp/jdk-8u202-linux-x64.rpm

#安装maven
ADD apache-maven-3.9.9-bin.tar.gz /usr/local/maven
RUN ln -s /usr/local/maven/apache-maven-3.9.9/bin/mvn /usr/bin/mvn
COPY settings.xml /usr/local/maven/apache-maven-3.9.9/conf/

#安装ffmpeg,ADD会自动解压
ADD ffmpeg-7.0.2-amd64-static.tar.xz /usr/local/ffmpeg
#设置环境变量
ENV FFMPEG_HOME /usr/local/ffmpeg/ffmpeg-7.0.2-amd64-static

#修改path
ENV PATH $FFMPEG_HOME:$PATH

#容器运行时自动挂载为匿名卷,存储静态资源,maven仓库
#写入的信息都不会记录进容器存储层，从而保证了容器存储层的无状态化
VOLUME [ "/data", "/repo", "/app", "/static" ]

#bashrc内的脚本，会在容器启动后自动运行

#修改文件描述符限制，防止mvn报错
RUN ulimit -n 65536
RUN echo ulimit -n 65536 >> ~/.bashrc

#切换用户,默认为0(root)
USER 1001

#每次启动后打包运行
ENTRYPOINT ["/bin/bash", "/app/startup.sh"]

EXPOSE 8080 8080