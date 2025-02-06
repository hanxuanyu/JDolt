package com.hxuanyu.jdolt.util;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;

import java.lang.reflect.Method;
import java.util.*;

public class MethodConstraintValidator {
    // 已调用的方法
    private final Set<String> calledMethods = new HashSet<>();

    // 记录方法与其互斥组的映射
    private final Map<String, String> methodToMutexGroup = new HashMap<>();

    // 记录方法与其依赖关系的映射
    private final Map<String, Set<String>> methodToDependencies = new HashMap<>();

    public MethodConstraintValidator(Class<?> clazz) {
        // 解析类中的方法注解
        for (Method method : clazz.getDeclaredMethods()) {
            // 解析互斥组
            if (method.isAnnotationPresent(MethodMutexGroup.class)) {
                MethodMutexGroup methodMutexGroup = method.getAnnotation(MethodMutexGroup.class);
                methodToMutexGroup.put(method.getName(), methodMutexGroup.value());
            }

            // 解析依赖关系
            if (method.isAnnotationPresent(MethodDependsOn.class)) {
                MethodDependsOn methodDependsOn = method.getAnnotation(MethodDependsOn.class);
                methodToDependencies.put(method.getName(), new HashSet<>(Arrays.asList(methodDependsOn.value())));
            }
        }
    }

    /**
     * 检查并标记方法调用
     */
    public void checkAndMark(String methodName) {
        // 检查互斥关系
        checkMutex(methodName);

        // 检查依赖关系
        checkDependencies(methodName);

        // 标记方法为已调用
        calledMethods.add(methodName);
    }

    /**
     * 检查互斥关系
     */
    private void checkMutex(String methodName) {
        String mutexGroup = methodToMutexGroup.get(methodName);
        if (mutexGroup != null) {
            for (Map.Entry<String, String> entry : methodToMutexGroup.entrySet()) {
                // 检查当前方法是否重复调用，或互斥组中的其他方法是否已调用
                if (entry.getValue().equals(mutexGroup) && calledMethods.contains(entry.getKey())) {
                    throw new IllegalStateException("Method '" + methodName + "' cannot be called because it conflicts with '" + entry.getKey() + "' in mutex group '" + mutexGroup + "'.");
                }
            }
        }
    }

    /**
     * 检查依赖关系
     */
    private void checkDependencies(String methodName) {
        Set<String> dependencies = methodToDependencies.get(methodName);
        if (dependencies != null) {
            boolean dependencySatisfied = false;
            for (String dependency : dependencies) {
                if (calledMethods.contains(dependency)) {
                    dependencySatisfied = true;
                    break;
                }
            }
            if (!dependencySatisfied) {
                throw new IllegalStateException("Method '" + methodName + "' cannot be called because it requires one of the following methods to be called first: " + dependencies);
            }
        }
    }
}