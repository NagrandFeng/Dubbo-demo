
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

#zookeeper java api使用入门

###1、首先需要在maven中添加依赖
```xml
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.8</version>
        </dependency>
```

###2、创建连接和回调接口
有关的java api
```java
ZooKeeper(String connectString, int sessionTimeout, Watcher watcher) throws IOException  
```
参数说明：
connection:zookeeper server列表, 可以是一个，如果存在多个server列表则以逗号隔开. ZooKeeper对象初始化后, 将从列表中选择一个server, 并尝试与其建立连接. 如果连接建立失败, 则会从列表的剩余项中选择一个server, 并再次尝试建立连接.
sessionTimeout：指定连接的超时时间
watcher：事件回调接口.(后面对这个接口做详细说明)
当连接成功建立后, 会回调watcher的process方法
建立连接的java实例代码如下：
```java
  /**
     * 连接zookeeper服务器
     *
     * @throws Exception
     */
    public void connect() throws Exception {
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, new ConnWatcher());
        // 等待连接完成
        connectedSignal.await();
        //创建基本节点
        /*try {
            createBasePath();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }*/
    }

    public class ConnWatcher implements Watcher {
        public void process(WatchedEvent event) {
            // 连接建立, 回调process接口时, 其event.getState()为KeeperState.SyncConnected
            if (event.getState() == Event.KeeperState.SyncConnected) {
                // 放开闸门, wait在connect方法上的线程将被唤醒
                connectedSignal.countDown();
                //打印连接信
                System.out.println("----- ----- event:  path=" + event.getPath() + ", typeValue=" + event.getType().getIntValue() + ", stateValue=" + event.getState().getIntValue());
            }
        }
    }
```
创建znode
ZooKeeper对象的create方法用于创建znode.

Java api 
String create(String path, byte[] data, List acl, CreateMode createMode); 
以下为各个参数的详细说明:
path:znode的路径.
data:与znode关联的数据.
acl. 指定权限信息, 如果不想指定权限, 可以传入Ids.OPEN_ACL_UNSAFE.
指定znode类型. CreateMode是一个枚举类, 从中选择一个成员传入即可
CreateMode的各种成员含义：
PERSISTENT： 创建的znode不会随着客户端连接的断开而删除
PERSISTENT_SEQUENTIAL ：创建的znode不会随着客户端连接的断开而删除，并且每次删除后，再次创建同样的名字将在data后面追加一个单调递增的值
EPHEMERAL：创建的znode是会随着客户端连接的断开而删除的
EPHEMERAL_SEQUENTIAL：创建的znode是会随着客户端连接的断开而删除的，并且每次删除后，再次创建同样的名字将在data后面追加一个单调递增的值

Java代码  
```java
 /**
     * 创建一个节点，如已存在则不会创建
     *
     * @param path for example : /testPath 或者 /testpath/test
     */
    public void createNode(String path) {
        //初次使用,ACL以及createMode都默认
        try {
            Stat stat = zk.exists(path, false);
            if (stat != null) {
                System.out.println("节点:" + path + "已存在,不需要创建");
                return;
            }
            String nodeCreate = zk.create(path, "init-data".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("node create:" + nodeCreate);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
```

获取子node列表
ZooKeeper对象的getChildren方法用于获取子node列表.

Java代码 
```java
List getChildren(String path, boolean watch); 
```
watch参数用于指定是否监听path node的子node的增加和删除事件, 以及path node本身的删除事件.

判断znode是否存在
ZooKeeper对象的exists方法用于判断指定znode是否存在.

```java
Stat exists(String path, boolean watch); 
```
watch参数用于指定是否监听path node的创建, 删除事件, 以及数据更新事件. 如果该node存在, 则返回该node的状态信息, 否则返回null.

获取node中关联的数据
ZooKeeper对象的getData方法用于获取node关联的数据.

```java
byte[] getData(String path, boolean watch, Stat stat); 
```
watch参数用于指定是否监听path node的删除事件, 以及数据更新事件, 注意, 不监听path node的创建事件, 因为如果path node不存在, 该方法将抛出KeeperException.NoNodeException异常.
stat参数是个传出参数, getData方法会将path node的状态信息设置到该参数中.

更新node中关联的数据
ZooKeeper对象的setData方法用于更新node关联的数据.
```java
Stat setData(final String path, byte data[], int version); 
```
data:待更新的数据.
version：参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败.，指定version为-1则忽略版本检查.
返回path node的状态信息.

删除znode
ZooKeeper对象的delete方法用于删除znode.

```java
void delete(final String path, int version); 
```
version参数的作用同setData方法.

###zookeeper中的Access Control（ACL）机制
1、在Zookeeper中，node的ACL是没有继承关系的，是独立控制的。Zookeeper的ACL，可以从三个维度来理解：scheme;user; permission，通常表示为scheme : id : permissions, 下面从这三个方面分别来介绍：

（1）scheme: 验证方式，也是scheme对应于采用哪种方案来进行权限管理，zookeeper实现了一个pluggable的ACL方案，可以通过扩展scheme，来扩展ACL的机制。
有下面几种scheme：
world: 它下面只有一个id, 叫anyone, world:anyone代表任何人，zookeeper中对所有人有权限的结点就是属于world:anyone的
auth: 它不需要id, 只要是通过authentication的user都有权限（zookeeper支持通过kerberos来进行authencation, 也支持username/password形式的authentication)
digest: 它对应的id为username:BASE64(SHA1(password))，它需要先通过username:password形式的authentication
ip: 它对应的id为客户机的IP地址，设置的时候可以设置一个ip段，比如ip:192.168.1.0/16, 表示匹配前16个bit的IP段
super: 在这种scheme情况下，对应的id拥有超级权限，可以做任何事情(cdrwa)

（2）id: id与scheme是紧密相关的，具体的情况参照scheme中的

（3）permission:
zookeeper目前支持下面一些权限：
CREATE(c): 创建权限，可以在在当前node下创建child node
DELETE(d): 删除权限，可以删除当前的node
READ(r): 读权限，可以获取当前node的数据，可以list当前node所有的child nodes
WRITE(w): 写权限，可以向当前node写数据
ADMIN(a): 管理权限，可以设置当前node的permission
就是在zkCli客户端中，输入getAcl命令看到的 cdrwa

###如何在zkCli中设置Acl
```
setAcl /test ip:127.0.0.1:crwda
```
表示设置路径我 /test的节点的 对应操作ip为127.0.0.1，拥有的权限是 cdrwa，也就是所有权限

###如何通过java api设置acl
```java 
     /**
     * 创建带有digest的node
     *
     * @param path
     */
    public void createNodeByDigest(String path) {
        //通过设置digest类型的scheme的acl创建znode
        List<ACL> acls = new ArrayList<ACL>();
        try {
            Stat basePathStat = zk.exists(path, false);
            if (basePathStat != null) {
                zk.delete(path, -1);
            }
            Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest("admin:yeshufeng"));
            ACL acl1 = new ACL(ZooDefs.Perms.ALL, id1);
            acls.add(acl1);
            zk.addAuthInfo("digest", "admin:yeshufeng".getBytes());
            String createNode = zk.create(path, "init-data".getBytes(), acls, CreateMode.PERSISTENT);
            System.out.println("create node:" + createNode);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }
```
generateDigest("admin:yeshufeng") ，参数为xxx:xxxx的形式的原因
其中，在DigestAuthenticationProvider.generateDigest(String idPassword)方法的源码中
将idPassword通过 ' : ' 切成两段，后面的代以我的知识就看不懂了~只能理解一个大概
源码如下：
```java
public static String generateDigest(String idPassword) throws NoSuchAlgorithmException {
	String[] parts = idPassword.split(":", 2);
	 byte[] digest = MessageDigest.getInstance("SHA1").digest(idPassword.getBytes());
	 return parts[0] + ":" + base64Encode(digest);
 }
```

###zookeeper的watcher机制





