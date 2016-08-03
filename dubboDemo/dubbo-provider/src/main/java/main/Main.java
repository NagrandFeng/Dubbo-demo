package main;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "applicationProvider.xml" });
        context.start();
        System.out.println("服务成功启动，输入任意字符退出");
        System.in.read();
	}
}
