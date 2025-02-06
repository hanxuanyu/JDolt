package com.hxuanyu.jdolt.util;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;

import java.lang.reflect.Method;
import java.util.*;

public class MethodConstraintValidator {
    // 已调用的方法
    private final Set<String> calledMethods = new HashSet<>();

    // 记录方法与其互斥组的映射（支持多组）
    private final Map<String, Set<String>> methodToMutexGroups = new HashMap<>();

    // 记录方法与其依赖关系的映射
    private final Map<String, Set<String>> methodToDependencies = new HashMap<>();

    public MethodConstraintValidator(Class<?> clazz) {
        // 解析类中的方法注解
        for (Method method : clazz.getDeclaredMethods()) {
            // 解析互斥组（支持多组）
            if (method.isAnnotationPresent(MethodMutexGroup.class)) {
                MethodMutexGroup methodMutexGroup = method.getAnnotation(MethodMutexGroup.class);
                methodToMutexGroups.put(
                        method.getName(),
                        new HashSet<>(Arrays.asList(methodMutexGroup.value()))
                );
            }

            // 解析依赖关系
            if (method.isAnnotationPresent(MethodDependsOn.class)) {
                MethodDependsOn methodDependsOn = method.getAnnotation(MethodDependsOn.class);
                methodToDependencies.put(
                        method.getName(),
                        new HashSet<>(Arrays.asList(methodDependsOn.value()))
                );
            }
        }
    }

    /**
     * 检查并标记方法调用
     */
    public void checkAndMark(String methodName) {
        checkMutex(methodName);
        checkDependencies(methodName);
        calledMethods.add(methodName);
    }

    /**
     * 检查互斥关系（支持多组）
     */
    private void checkMutex(String methodName) {
        Set<String> groups = methodToMutexGroups.get(methodName);
        if (groups != null) {
            // 遍历当前方法的所有互斥组
            for (String group : groups) {
                // 检查所有属于同组的方法
                for (Map.Entry<String, Set<String>> entry : methodToMutexGroups.entrySet()) {
                    String otherMethod = entry.getKey();
                    Set<String> otherGroups = entry.getValue();

                    // 如果发现同组且已调用的方法
                    if (otherGroups.contains(group)
                            && !otherMethod.equals(methodName)
                            && calledMethods.contains(otherMethod)) {
                        throw new IllegalStateException(
                                "Method '" + methodName + "' cannot be called. " +
                                        "Conflict with '" + otherMethod + "' in mutex group '" + group + "'."
                        );
                    }
                }
            }
        }
    }

    /**
     * 检查依赖关系（支持多组）
     */
    private void checkDependencies(String methodName) {
        Set<String> dependencies = methodToDependencies.get(methodName);
        if (dependencies != null && !dependencies.isEmpty()) {
            boolean satisfied = false;
            for (String dep : dependencies) {
                if (calledMethods.contains(dep)) {
                    satisfied = true;
                    break;
                }
            }
            if (!satisfied) {
                throw new IllegalStateException(
                        "Method '" + methodName + "' requires at least one of: " + dependencies
                );
            }
        }
    }
}