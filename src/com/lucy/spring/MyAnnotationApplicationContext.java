package com.lucy.spring;

import com.lucy.spring.annotation.Component;
import com.lucy.spring.annotation.ComponentScan;
import com.lucy.spring.annotation.Scope;
import com.lucy.spring.exception.NotFoundBeanDefinitionException;
import com.lucy.spring.pojo.BeanDefinition;
import com.lucy.utils.StringUtils;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyAnnotationApplicationContext {

    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Object> objectMap = new ConcurrentHashMap<>();

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
        //扫描并注册bean
        if (isScanAnnotation) {
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);

            //获取类加载器
            ClassLoader classLoader = this.getClass().getClassLoader();
            String[] packages = componentScan.value();
            for (String packageName : packages) {
                String path = packageName.replace(".", "/");
//                System.out.println(path);
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
//                        System.out.println(beanName);
                        //获取完整类名
                        String completeName = packageName + "." + beanName;
//                        System.out.println(completeName);


                        //通过反射获取类文件
                        try {
                            Class<?> clazz = classLoader.loadClass(completeName);
                            if (clazz.isAnnotationPresent(Component.class)) {
                                //获取名称
                                Component component = clazz.getAnnotation(Component.class);
                                String name = component.value();
                                if (StringUtils.isEmpty(name)) {
                                    name = Introspector.decapitalize(clazz.getSimpleName());
                                }

                                //注册组件
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);
                                //判断注册方式
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                    beanDefinition.setScope(StringUtils.isEmpty(scopeAnnotation.value()) ? "singleton" : scopeAnnotation.value());
                                } else {
                                    //默认为单例
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(name, beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }

        }

        //实例化单例bean对象
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            if ("singleton".equals(entry.getValue().getScope())) {
                objectMap.put(entry.getKey(), createBean(entry.getValue()));
            }
        }
    }

    public Object getBean(String beanName) throws NotFoundBeanDefinitionException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NotFoundBeanDefinitionException(beanName);
        }

        String scope = beanDefinition.getScope();
        Object bean;
        if ("singleton".equals(scope)) {
            bean = objectMap.get(beanName);
            if (bean == null) {
                //创建实例
                bean = createBean(beanDefinition);
                objectMap.put(beanName, bean);
            }
        } else {
            bean = createBean(beanDefinition);
        }
        return bean;
    }

    /**
     * 创建bean实例
     * @param beanDefinition
     * @return
     */
    private Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
