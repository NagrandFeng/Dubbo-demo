
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

##dubbo的配置文件中各标签的含义
```xml
<dubbo:reference/> 引用配置，用于创建一个远程服务代理，一个引用可以指向多个注册中心。
<dubbo:protocol/> 协议配置，用于配置提供服务的协议信息，协议由提供方指定，消费方被动接受。
<dubbo:application/> 应用配置，用于配置当前应用信息，不管该应用是提供者还是消费者。
<dubbo:module/> 模块配置，用于配置当前模块信息，可选。
<dubbo:registry/> 注册中心配置，用于配置连接注册中心相关信息。
<dubbo:monitor/> 监控中心配置，用于配置连接监控中心相关信息，可选。
<dubbo:provider/> 提供方的缺省值，当ProtocolConfig和ServiceConfig某属性没有配置时，采用此缺省值，可选。
<dubbo:consumer/> 消费方缺省配置，当ReferenceConfig某属性没有配置时，采用此缺省值，可选。
<dubbo:method/> 方法配置，用于ServiceConfig和ReferenceConfig指定方法级的配置信息。
<dubbo:argument/> 用于指定方法参数配置。
```
###dubbo协议配置
<dubbo:protocol name="dubbo" dispatcher="all" threadpool="fixed" threads="100" />
protocol,必填项，缺省协议为dubbo，采用单一长连接和NIO异步通讯，适合小数据量大并发的服务调用，以及服务消费者机器数远大于服务提供者机器数的情况

每服务每提供者每消费者使用单一长连接，如果数据量较大，可以使用多个连接

port：可选项，dubbo协议缺省端口为20880，rmi协议缺省端口为1099，http和hessian协议缺省端口为80。如果配置为-1或者没有配置port，则会分配一个没有被占用的端口。Dubbo 2.4.0+，分配的端口在协议缺省端口的基础上增长，确保端口段可控。

threadpool的值
fixed:固定大小线程池，启动时建立线程，不关闭，一直持有（缺省）
cached 缓存线程池，空闲一分钟自动删除，需要时重建
limited 可伸缩线程池，但池中的线程数只会增长不会收缩

在dubbo.properties中的配置：dubbo.service.protocol=dubbo


###dubbo:application 配置
name:必填项，当前应用名称，用于注册中心计算应用间依赖关系，注意：消费者和提供者应用名不要一样，此参数不是匹配条件，你当前项目叫什么名字就填什么，和提供者消费者角色无关，比如：kylin应用调用了morgan应用的服务，则kylin项目配成kylin，morgan项目配成morgan，可能kylin也提供其它服务给别人使用，但kylin项目永远配成kylin，这样注册中心将显示kylin依赖于morgan


###dubbo:registry配置
id:可选项，注册中心引用BeanId，可以在<dubbo:service registry="">或<dubbo:reference registry="">中引用此ID

address:必填项，注册中心服务器地址，如果地址没有端口缺省为9090，同一集群内的多个地址用逗号分隔，如：ip:port,ip:port，不同集群的注册中心，请配置多个<dubbo:registry>标签


###dubbo:service配置
interface:必填项，服务接口名

ref: 必填项，服务对象实现引用


###dubbo:consumer配置
无必填项
上面使用到的timeout : 表示服务调用超时时间(ms)

