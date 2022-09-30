package com.lucy.spring.exception;

public class NotFoundBeanDefinitionException extends Exception {

    public NotFoundBeanDefinitionException(String message) {
        System.out.println(String.format("未注入%s", message));
    }
}
