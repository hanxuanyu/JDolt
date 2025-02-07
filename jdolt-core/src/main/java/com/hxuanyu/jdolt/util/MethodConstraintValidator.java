package com.hxuanyu.jdolt.util;

import com.hxuanyu.jdolt.annotation.MethodAllowGroup;
import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodExclusive;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;

import java.lang.reflect.Method;
import java.util.*;

public class MethodConstraintValidator {
    // 已调用的方法
    private final Set<String> calledMethods = new HashSet<>();

    // 方法与其互斥组的映射
    private final Map<String, Set<String>> methodToMutexGroups = new HashMap<>();

    // 方法与其允许共存组的映射
    private final Map<String, Set<String>> methodToAllowGroups = new HashMap<>();

    // 方法与其依赖关系的映射
    private final Map<String, Set<String>> methodToDependencies = new HashMap<>();

    // 唯一调用方法集合
    private final Set<String> exclusiveMethods = new HashSet<>();

    // 必须调用的方法集合
    private final Set<String> requiredMethods = new HashSet<>();

    private final Class<?> clazz;

    public MethodConstraintValidator(Class<?> clazz) {
        this.clazz = clazz; // 保存类引用
        for (Method method : clazz.getDeclaredMethods()) {
            // 检查注解互相不能共存
            boolean hasMutex = method.isAnnotationPresent(MethodMutexGroup.class);
            boolean hasAllow = method.isAnnotationPresent(MethodAllowGroup.class);
            boolean hasExclusive = method.isAnnotationPresent(MethodExclusive.class);

            if ((hasMutex && hasAllow) || (hasMutex && hasExclusive) || (hasAllow && hasExclusive)) {
                throw new IllegalStateException(
                        "Method '" + method.getName() + "' cannot have conflicting annotations: " +
                                "@MethodMutexGroup, @MethodAllowGroup, and @MethodExclusive cannot coexist."
                );
            }

            // 解析互斥组
            if (hasMutex) {
                MethodMutexGroup methodMutexGroup = method.getAnnotation(MethodMutexGroup.class);
                methodToMutexGroups.put(
                        method.getName(),
                        new HashSet<>(Arrays.asList(methodMutexGroup.value()))
                );
            }

            // 解析允许共存组
            if (hasAllow) {
                MethodAllowGroup methodAllowGroup = method.getAnnotation(MethodAllowGroup.class);
                methodToAllowGroups.put(
                        method.getName(),
                        new HashSet<>(Arrays.asList(methodAllowGroup.value()))
                );
            }

            // 解析唯一调用
            if (hasExclusive) {
                exclusiveMethods.add(method.getName());
            }

            // 解析依赖关系
            if (method.isAnnotationPresent(MethodDependsOn.class)) {
                MethodDependsOn methodDependsOn = method.getAnnotation(MethodDependsOn.class);
                methodToDependencies.put(
                        method.getName(),
                        new HashSet<>(Arrays.asList(methodDependsOn.value()))
                );
            }

            // 解析必须调用
            if (method.isAnnotationPresent(MethodInvokeRequired.class)) {
                requiredMethods.add(method.getName());
            }
        }
    }

    /**
     * 检查并标记方法调用
     */
    public void checkAndMark(String methodName) {
        checkExclusive(methodName);
        checkMutex(methodName);
        checkAllowGroup(methodName);
        checkDependencies(methodName);
        calledMethods.add(methodName);
    }

    /**
     * 检查互斥关系
     */
    private void checkMutex(String methodName) {
        // 检查与其他方法的互斥组冲突
        Set<String> groups = methodToMutexGroups.get(methodName);
        if (groups != null) {
            for (String calledMethod : calledMethods) {
                Set<String> calledMethodGroups = methodToMutexGroups.get(calledMethod);
                if (calledMethodGroups != null) {
                    for (String group : groups) {
                        if (calledMethodGroups.contains(group)) {
                            throw new IllegalStateException(
                                    "Method '" + methodName + "' cannot be called. " +
                                            "Conflict with previously called method '" + calledMethod + "' " +
                                            "in mutex group '" + group + "'."
                            );
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查允许共存组
     */
    private void checkAllowGroup(String methodName) {
        Set<String> allowGroup = methodToAllowGroups.get(methodName);
        if (allowGroup != null) {
            // 遍历所有已调用的方法，检查是否都在允许共存组中
            for (String calledMethod : calledMethods) {
                if (!allowGroup.contains(calledMethod)) {
                    throw new IllegalStateException(
                            "Method '" + methodName + "' cannot be called. " +
                                    "Conflict with previously called method '" + calledMethod + "'. " +
                                    "Only the following methods are allowed to coexist: " + allowGroup
                    );
                }
            }
        }
    }

    /**
     * 检查依赖关系
     */
    private void checkDependencies(String methodName) {
        Set<String> dependencies = methodToDependencies.get(methodName);
        if (dependencies != null && !dependencies.isEmpty()) {
            // 获取当前方法的依赖注解
            MethodDependsOn methodDependsOn = getMethodDependsOnAnnotation(methodName);
            boolean allRequired = methodDependsOn != null && methodDependsOn.allRequired();

            if (allRequired) {
                // 如果要求所有依赖方法都被调用
                for (String dep : dependencies) {
                    if (!calledMethods.contains(dep)) {
                        throw new IllegalStateException(
                                "Method '" + methodName + "' requires all of the following methods to be called: " + dependencies
                        );
                    }
                }
            } else {
                // 如果只要求至少一个依赖方法被调用
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

    /**
     * 获取方法的 MethodDependsOn 注解
     */
    private MethodDependsOn getMethodDependsOnAnnotation(String methodName) {
        try {
            Method method = clazz.getDeclaredMethod(methodName);
            return method.getAnnotation(MethodDependsOn.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 检查唯一调用约束
     */
    private void checkExclusive(String methodName) {
        if (exclusiveMethods.contains(methodName)) {
            if (!calledMethods.isEmpty()) {
                throw new IllegalStateException(
                        "Method '" + methodName + "' must be the only method called. " +
                                "Other methods have already been called: " + calledMethods
                );
            }
        } else {
            for (String calledMethod : calledMethods) {
                if (exclusiveMethods.contains(calledMethod)) {
                    throw new IllegalStateException(
                            "Method '" + methodName + "' cannot be called because exclusive method '" +
                                    calledMethod + "' has already been called."
                    );
                }
            }
        }
    }

    /**
     * 检查所有标注了 @MethodInvokeRequired 的方法是否都被调用
     */
    public void checkRequired() {
        for (String requiredMethod : requiredMethods) {
            if (!calledMethods.contains(requiredMethod)) {
                throw new IllegalStateException(
                        "Method '" + requiredMethod + "' is required to be called but was not."
                );
            }
        }
    }
}