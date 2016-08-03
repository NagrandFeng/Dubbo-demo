package demo;


import entity.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.IHelloString;
import service.UserService;

public class Consumer {

	public String reverseString() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationConsumer.xml" });
		context.start();
		IHelloString stringService = (IHelloString) context.getBean("stringService");
		//消费者需要知道服务提供方注册方法的返回类型，参数等基本信息
//		System.out.println(stringService.changeString("hello world"));
		return stringService.changeString("hello world");
	}

	public User[] getAll(){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationConsumer.xml" });
		context.start();
		UserService userService = (UserService) context.getBean("userService");
		return userService.getAllUser();
	}

	public User getUser(Integer id){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationConsumer.xml" });
		context.start();
		UserService userService = (UserService) context.getBean("userService");
		return userService.getUser(id);
	}
}
