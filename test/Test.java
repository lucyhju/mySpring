import com.lucy.config.WebConfig;
import com.lucy.controller.UserController;
import com.lucy.spring.MyAnnotationApplicationContext;
import com.lucy.spring.exception.NotFoundBeanDefinitionException;

public class Test {

    public static void main(String[] args) {
        MyAnnotationApplicationContext applicationContext = new MyAnnotationApplicationContext(WebConfig.class);
        try {
//            UserController userController = (UserController) applicationContext.getBean("UserController");
            System.out.println(applicationContext.getBean("UserController"));
            System.out.println(applicationContext.getBean("UserController"));
            System.out.println(applicationContext.getBean("UserController"));
            System.out.println(applicationContext.getBean("UserController"));
            System.out.println(applicationContext.getBean("UserController"));
            System.out.println(applicationContext.getBean("UserController"));
        } catch (NotFoundBeanDefinitionException e) {
            e.printStackTrace();
        }
    }
}
