package com.lucy.spring;

import com.lucy.spring.annotation.Component;
import com.lucy.spring.annotation.ComponentScan;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class MyAnnotationApplicationContext {

    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public MyAnnotationApplicationContext(Class configClass) {
        this.configClass = configClass;

        scanAnnotationAndRegisterComponent(configClass);

    }

    /**
     * 扫描注解并注册主键
     * @param configClass
     */
    private void scanAnnotationAndRegisterComponent(Class configClass) {
        //扫描注解
        boolean isScanAnnotation = configClass.isAnnotationPresent(ComponentScan.class);
        if (isScanAnnotation) {
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);

            //获取类加载器
            ClassLoader classLoader = this.getClass().getClassLoader();
            String[] packages = componentScan.value();
            for (String packageName : packages) {
                String path = packageName.replace(".", "/");
                System.out.println(path);
                URL resource = classLoader.getResource(path);
                File file = new File(resource.getFile());
                if (file.isDirectory()) {
                    //获取class文件列表
                    File[] files = file.listFiles();
                    for (File f : files) {
                        String absolutePath = f.getAbsolutePath();

                        if (!absolutePath.endsWith(".class")) {
                            //跳过非class文件
                            continue;
                        }

                        absolutePath = absolutePath.replace("\\", "/");
                        //获取类名
                        String beanName = absolutePath.substring(absolutePath.indexOf(path) + path.length() + 1, absolutePath.indexOf(".class"));
                        System.out.println(beanName);
                        //获取完整类名
                        String completeName = packageName + "." + beanName;
                        System.out.println(completeName);


                        //通过反射获取类文件
                        try {
                            Class<?> clazz = classLoader.loadClass(completeName);
                            if (clazz.isAnnotationPresent(Component.class)) {
                                //注册组件

                            }
                        } catch (ClassNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public Object getBean(String beanName) {
        return null;
    }
}
