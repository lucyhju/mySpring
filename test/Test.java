import com.lucy.config.WebConfig;
import com.lucy.controller.UserController;
import com.lucy.spring.MyAnnotationApplicationContext;

public class Test {

    public static void main(String[] args) {
        MyAnnotationApplicationContext applicationContext = new MyAnnotationApplicationContext(WebConfig.class);
        UserController userController = (UserController) applicationContext.getBean("UserController");
    }
}
