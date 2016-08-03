package service.impl;

import service.IHelloString;

public class HelloStringImpl implements IHelloString{

	public String changeString(String str) {
		StringBuffer sb=new StringBuffer();
		sb.append("方法调用成功，此方法由服务方提供\n");
		sb.append("服务消费者输入参数:"+str+"\n");
		sb.append("倒转字符串后: ");
		for (int i = str.length()-1; i>=0; i--) {
			sb.append(str.charAt(i));
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
	
}
