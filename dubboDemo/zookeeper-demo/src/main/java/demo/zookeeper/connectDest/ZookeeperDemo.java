package demo.zookeeper.connectDest;

import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ZookeeperDemo {
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        String zookeeperUrl = "127.0.0.1:2181";
        int timeout = 3000;
        try {
            Watcher watcher = new Watcher() {

                public void process(WatchedEvent event) {
                    System.out.println("----- ----- event:  path=" + event.getPath() + ", typeValue=" + event.getType().getIntValue() + ", stateValue=" + event.getState().getIntValue());

                }

            };
            ZooKeeper zookeeper = new ZooKeeper(zookeeperUrl, timeout, watcher);

            // creat base path
            String basePath = "/test_java";
            Stat basePathStat = zookeeper.exists(basePath, false);
            if (basePathStat != null) {
                System.out.println("----- exist path: " + basePath);
            } else {
                zookeeper.create(basePath, "test_java".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                System.out.println("----- create path:" + basePath);
            }

            // create child path
            String childPath1 = basePath + "/data1";
            String childPath2 = basePath + "/data2";

            if (zookeeper.exists(childPath1, false) != null) {
                zookeeper.delete(childPath1, -1);
            }
            if (zookeeper.exists(childPath2, false) != null) {
                zookeeper.delete(childPath2, -1);
            }

            String child1Result = zookeeper.create(childPath1, "child_data_PERSISTENT".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("----- child1 create result: " + child1Result);
            String child2Result = zookeeper.create(childPath2, "child_data2_PERSISTENT_SEQUENTIAL".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
            System.out.println("----- child2 create result: " + child2Result);
            String child3Result = zookeeper.create(childPath2, "child_data2_EPHEMERAL".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("----- child3 create result: " + child3Result);
            String child4Result = zookeeper.create(childPath2, "child_data2_EPHEMERAL_SEQUENTIAL".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("----- child4 create result: " + child4Result);

            byte[] child1Bytes = zookeeper.getData(childPath1, false, zookeeper.exists(childPath1, false));
            System.out.println("----- child1Bytes: " + new String(child1Bytes));

            Stat child1Stat = zookeeper.setData(childPath1, "newChildData1".getBytes(), -1);
            byte[] child1NewBytes = zookeeper.getData(childPath1, false, child1Stat);
            System.out.println("----- child1NewBytes: " + new String(child1NewBytes));

            zookeeper.delete(childPath1, -1);
            zookeeper.delete(childPath2, -1);
//            zookeeper.delete(basePath, -1);

            zookeeper.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}