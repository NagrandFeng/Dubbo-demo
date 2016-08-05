package demo.zookeeper.connection;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.*;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ZkConnection {
    private String hosts = "127.0.0.1:2181";
    private static final int SESSION_TIMEOUT = 5000;
    private CountDownLatch connectedSignal = new CountDownLatch(1);
    protected ZooKeeper zk;
    private final String basePath = "/nagrand";

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

    public void close() throws InterruptedException {
        zk.close();

    }

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

    /**
     * 删除一个非空的节点，节点为空只能考虑递归删除
     *
     * @param path
     */
    public void deleteNotEmpty(String path) {
        try {
            zk.delete(path, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

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

    /**
     * 得到节点的子节点列表
     *
     * @param path
     * @return
     */
    public List<String> getChildPath(String path) {
        List<String> childs = new ArrayList<String>();
        try {
            childs = zk.getChildren(path, false); //第二参数可以是 Wacher类型或是 boolean
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return childs;
    }


    /**
     * 得到节点的值
     *
     * @param path
     * @return
     * @throws Exception
     */
    public byte[] getData(String path) throws Exception {
        Stat stat = zk.exists(path, false);
        return zk.getData(path, false, stat);
    }

    /**
     * 得到指定路径节点的ACL列表
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<ACL> getAcl(String path) throws Exception {
        Stat stat = zk.exists(path, false);
        return zk.getACL(path, stat);
    }

    public void deleteDigui(String path) throws Exception {
        Stat stat = zk.exists(path,false);
        if (stat == null) {
            System.out.println("");
        }
        this.deletePath(path, zk);
    }

    /**
     * zookeeper不允许直接删除含有子节点的节点;如果你需要删除当前节点以及其所有子节点,需要递归来做
     *
     * @param path
     * @param zooKeeper
     * @throws Exception
     */
    private void deletePath(String path, ZooKeeper zooKeeper) {
        try {
            List<String> children = zooKeeper.getChildren(path, false);
            for (String child : children) {
                String childPath = path + "/" + child;
                deletePath(childPath, zooKeeper);
            }
            zooKeeper.delete(path, -1);
        } catch (KeeperException.NoNodeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    //删除节点,删除时比较version,避免删除时被其他client修改
    public boolean delete(String path) {
        try {
            Stat stat = zk.exists(path, false);
            //如果节点已经存在
            if (stat != null) {
                zk.delete(path, stat.getVersion());
            }
        } catch (KeeperException.NoNodeException e) {
            //igore
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String createBasePath() throws InterruptedException, KeeperException {
        // creat base path
        String result = "";
        Stat basePathStat = zk.exists(basePath, false);
        if (basePathStat != null) {
            System.out.println("----- exist path: " + basePath);
            result = basePath;
        } else {
            //CreateMode.PERSISTENT表示创建的节点不会随着客户端链接的断开而删除
            result = zk.create(basePath, basePath.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("----- create path:" + basePath);

        }
        return result;
    }
}
