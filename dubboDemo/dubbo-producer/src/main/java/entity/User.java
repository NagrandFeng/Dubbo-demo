package entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/3.
 */
public class User implements Serializable{


    private Integer id;
    private String username;
    private Integer age;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        String s="id: "+id+" ,username: "+username+" ,age: "+age;

        return s;
    }
}
