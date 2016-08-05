package demo;

import entity.User;

public class main {
    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        System.out.println(consumer.sayHello("*"));
    }
    public static void test(){
        Consumer[] consumers=new Consumer[10];
        for (int i = 0; i < consumers.length; i++) {
            Consumer consumer = new Consumer();
            consumers[i]=consumer;
        }
        /*for (int i = 0; i < 10; i++) {
            consumers[i].reverseString();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        Consumer userConsumer=new Consumer();
        User[] users=userConsumer.getAll();
        for (User user:users) {
            System.out.println(user.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
      /*  User user=userConsumer.getUser(20);
        System.out.println(user.toString());*/
        for (int i = 0; i < 10; i++) {
            User user=consumers[i].getUser(10+i);
            System.out.println(user.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
