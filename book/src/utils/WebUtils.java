package utils;

import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author HALF
 * @create 2021-08-21 21:26
 */
public class WebUtils {
    /**
     * 把map中的值注入到对应的Javabean属性中
     * @param value
     * @param bean
     */
    public static <T> T copyParamToBean(Map value, T bean){
        /**
         * 把所有请求的参数注入到user对象中
         */
        try {
            BeanUtils.populate(bean, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 将字符串转换成为 int 类型的数
     * @param strInt
     * @param defaultValue
     * @return
     */
    public static int parseInt(String strInt, int defaultValue) {
        try {
            return Integer.parseInt(strInt);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return defaultValue;
    }
}
