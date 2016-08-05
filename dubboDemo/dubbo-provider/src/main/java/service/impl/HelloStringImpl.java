package service.impl;

import service.IHelloString;

public class HelloStringImpl implements IHelloString {

    public String changeString(String str) {
        StringBuffer sb = new StringBuffer();
        sb.append("方法调用成功，此方法由服务方提供\n");
        sb.append("服务消费者输入参数:" + str + "\n");
        sb.append("倒转字符串后: ");
        for (int i = str.length() - 1; i >= 0; i--) {
            sb.append(str.charAt(i));
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    /**
     * print strange string
     *
     * @param input
     * @return
     */
    public String sayHello(String input) {
        int[] array = {0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 4, 5, 2, 3, 4, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < array.length; i++) {
            if (i % 7 == 0) {
                sb.append("  \n");
            }

            if (array[i] == 0) {
                sb.append("   ");
            } else if (array[i] == 4) {
                sb.append("  ");
            } else if (array[i] == 5) {
                sb.append(" I ");
            } else if (array[i] == 2) {
                sb.append("Love ");
            } else if (array[i] == 3) {
                sb.append("You");
            } else {
                sb.append("  " + input);
            }
        }
        return sb.toString();

    }
}
