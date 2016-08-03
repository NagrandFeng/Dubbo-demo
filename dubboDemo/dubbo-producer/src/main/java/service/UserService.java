package service;

import entity.User;

/**
 * Created by Administrator on 2016/8/3.
 */
public interface UserService {
    public User[] getAllUser();
    public User getUser(Integer id);
}
