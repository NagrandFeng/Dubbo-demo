package demo.zookeeper.main;

import demo.zookeeper.connection.ZkConnection;
import org.apache.zookeeper.data.ACL;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
//        zkConnection.createNode("/nagrand/t3/t8/t9");
//        zkConnection.deleteDigui("/nagrand");
//        List<String> result=zkConnection.getChildPath("/");
        List<String> result=zkConnection.getChild("/nagrand");
        result=encodeUrl(result);
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

    /**
     * 还原url的格式，学习时为了查看原本格式做的转码
     * @param stringList
     * @return
     */
    public static List<String> encodeUrl(List<String> stringList){
        List<String> result=new ArrayList<String>();
        for (String str:stringList) {
            str = str.replaceAll("%3A", ":").replaceAll("%2F", "/")  //过滤URL 包含中文
                    .replaceAll("%3F", "?").replaceAll("%3D", "=").replaceAll(
                            "%26", "&");
            result.add(str);
        }
        return result;
    }
}
