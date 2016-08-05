package demo.zookeeper.main;

import demo.zookeeper.connection.ZkConnection;
import org.apache.zookeeper.data.ACL;

import java.util.List;

/**
 * Created by Administrator on 2016/8/4.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        ZkConnection zkConnection=new ZkConnection();
        zkConnection.connect();
//        zkConnection.createNode("testhello");
//        byte[] childData=zkConnection.getData("/nagrand/testhello");
//        System.out.println("data:"+new String(childData));
//        zkConnection.delete("/nagrand");
//        zkConnection.createNode("/nagrand/test3");
//        zkConnection.deleteDigui("/nagrand");
        List<String> result=zkConnection.getChildPath("/nagrand");
        for (String childPath:result) {
            System.out.println(childPath);
        }
        /*zkConnection.createNodeByDigest("/digest");
        byte[] childData=zkConnection.getData("/digest");
        System.out.println("data:"+new String(childData));
        List<ACL> acls=zkConnection.getAcl("/digest");
        for (ACL acl:acls) {
            System.out.println(acl.toString());
            System.out.println("id:"+acl.getId()+"\n perms :"+acl.getPerms());
        }*/
        zkConnection.close();
    }
}
