package com.bigtreetc.sample.base.utils;

import java.lang.reflect.Method;
import lombok.val;

public class ReflectionUtils {

  public static void invoke(Object instance, Method handler, Object arg) {
    try {
      handler.invoke(instance, arg);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static <T> T newInstance(Class<T> declaringClass) {
    try {
      val constructor = declaringClass.getConstructor();
      return constructor.newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }
}
