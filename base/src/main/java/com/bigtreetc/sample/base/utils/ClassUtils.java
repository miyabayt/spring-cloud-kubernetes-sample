package com.bigtreetc.sample.base.utils;

import lombok.val;

public class ClassUtils {

  @SuppressWarnings("unchecked")
  public static <T> Class<T> getClass(Class<T> clazz, String name) {
    try {
      val classLoader = org.springframework.util.ClassUtils.getDefaultClassLoader();
      if (classLoader != null) {
        return (Class<T>) classLoader.loadClass(name);
      }
      return (Class<T>) Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
