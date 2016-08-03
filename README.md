
# Dubbo-demo
provider and consumer demo

#简介
项目的目录结构如下：
dubbo-provider是服务提供方
dubbo-consumer是服务消费者
zookeeper-3.4.8.tar.gz是zookeeper的windows环境安装包

dubbo-provider服务提供方需要用到zookeeper注册服务

##zookeeper在windows下的环境安装
1、下载上面的zookeeper-3.4.8.tar.gz到本地，完成解压

2、在conf目录下增加一个zoo.cfg文件，可以直接把zoo_sample.cfg改为zoo.cfg，zookeeper在启动时会自动寻找conf下的zoo.cfg配置文件，zoo.cfg的
默认内容为
```xml
# The number of milliseconds of each tick 心跳间隔 毫秒每次
tickTime=2000
# The number of ticks that the initial 
# synchronization phase can take
initLimit=10
# The number of ticks that can pass between 
# sending a request and getting an acknowledgement
syncLimit=5
# the directory where the snapshot is stored.   //镜像数据位置
# do not use /tmp for storage, /tmp here is just 
# example sakes.
dataDir=/tmp/zookeeper
# the port at which the clients will connect 客户端连接的端口
clientPort=2181
# the maximum number of client connections.
# increase this if you need to handle more clients
#maxClientCnxns=60
#
# Be sure to read the maintenance section of the 
# administrator guide before turning on autopurge.
#
# http://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
#
# The number of snapshots to retain in dataDir
#autopurge.snapRetainCount=3
# Purge task interval in hours
# Set to "0" to disable auto purge feature
#autopurge.purgeInterval=1
```
可以直接使用默认的配置
3、cmd下进入安装目录的bin目录下，运行zkServer.cmd就能够看到服务的启动进程

4、再此启动一个cmd窗口，进入bin目录下，通过命令zkCli启动客户端运行，查看服务器的状态，在这个窗口，输入help命令可以看到其他命令的用法

5、到这里，就成功开启了一个本地的zookeeper服务器，端口为2181

##完成provider服务提供者

服务提供者对外暴露接口IHelloString.changeString,UserService.getAllUser,UserService.getUser
接口的实现参见demo中的代码
在配置文件中的配置服务暴露方法为
```xml
	<!-- 提供方的应用信息，用于计算依赖关系 -->
	<dubbo:application name="provider-of-service"/>
	
	 <!--使用zookeeper注册，需要在本地配置zookeeper -->
	<dubbo:registry protocol="zookeeper" address="127.0.0.1:2181" />
	<dubbo:protocol name="dubbo" port="20880" />

	<!-- 和本地服务一样实现远程服务 -->
	<bean id="stringService" class="service.impl.HelloStringImpl" />
	<bean id="userService" class="service.impl.UserServiceImpl"/>

	<!-- 服务提供者声明需要暴露的服务接口 -->
	<dubbo:service interface="service.IHelloString " token="true" ref="stringService" executes="10" />
	<dubbo:service interface="service.UserService" token="true" ref="userService" executes="10"  />
```
##完成consumer服务消费者

消费者在Pom.xml中加入对服务提供者的依赖，直接使用服务提供者的接口，接口可以在配置文件中的配置如下
```xml
	<!-- 服务使用方应用名，用于计算依赖关系，不是匹配条件,不要和提供方一样，以免造成不必要的麻烦 -->
	<dubbo:application name="consumer-of-service" />

	<!-- 使用zookeeper注册,需要在本地开启一个zookeeper注册服务 -->
	 <dubbo:registry protocol="zookeeper" address="127.0.0.1:2181" />
	<dubbo:consumer timeout="100" />
	<!-- 生成远程服务代理，和本地bean一样使用stringService -->
	<dubbo:reference id="stringService" interface="service.IHelloString" />
	<dubbo:reference id="userService" interface="service.UserService" />
```
