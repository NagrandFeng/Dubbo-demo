package service.impl;

import entity.User;
import service.UserService;

/**
 * Created by Administrator on 2016/8/3.
 */
public class UserServiceImpl implements UserService {
    public User[] getAllUser() {
        User[] users=new User[10];
        for (int i = 0; i < 10; i++) {
            User user=new User();
            user.setId(i);
            user.setUsername("name"+i);
            user.setAge(20+i);
            users[i]=user;
        }
        return users;
    }

    public User getUser(Integer id) {
        User user=new User();
        user.setId(id);
        user.setUsername("getUserByid");
        user.setAge(55);
        return user;
    }
}
