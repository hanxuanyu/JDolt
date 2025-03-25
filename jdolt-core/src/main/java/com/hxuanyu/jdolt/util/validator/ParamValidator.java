package com.hxuanyu.jdolt.util.validator;

import java.util.*;

/**
 * 参数验证器，用于验证dolt相关方法的参数列表
 *
 * @author hanxuanyu
 * @version 2.2
 */
public class ParamValidator {

    private final String[] params;
    private final List<String> errors = new ArrayList<>();

    // 私有构造函数，防止直接实例化
    private ParamValidator(String[] params) {
        this.params = params;
    }

    // 静态方法创建验证器对象
    public static ParamValidator create(String[] params) {
        return new ParamValidator(params);
    }

    // 检查参数是否有重复
    public ParamValidator checkNoDuplicates() {
        if (!noDuplicates()) {
            errors.add("Parameters contain duplicates.");
        }
        return this;
    }

    public boolean noDuplicates() {
        Set<String> set = new HashSet<>(Arrays.asList(params));
        return set.size() == params.length;
    }

    // 检查参数数量是否等于指定值
    public ParamValidator checkSizeEquals(int size) {
        if (!sizeEquals(size)) {
            errors.add("Parameter count must be exactly " + size + ".");
        }
        return this;
    }

    public boolean sizeEquals(int size) {
        return params.length == size;
    }

    // 检查参数数量是否小于指定值
    public ParamValidator checkSizeLessThan(int size) {
        if (!sizeLessThan(size)) {
            errors.add("Parameter count must be less than " + size + ".");
        }
        return this;
    }

    public boolean sizeLessThan(int size) {
        return params.length < size;
    }

    // 检查参数数量是否大于指定值
    public ParamValidator checkSizeGreaterThan(int size) {
        if (!sizeGreaterThan(size)) {
            errors.add("Parameter count must be greater than " + size + ".");
        }
        return this;
    }

    public boolean sizeGreaterThan(int size) {
        return params.length > size;
    }

    // 检查参数是否非空
    public ParamValidator checkNotEmpty() {
        if (!notEmpty()) {
            errors.add("Parameters cannot be null or empty.");
        }
        return this;
    }

    public boolean notEmpty() {
        if (params == null || params.length == 0) {
            return false;
        }
        for (String param : params) {
            if (param == null || param.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // 检查参数内容是否包含于给定集合
    public ParamValidator checkContainedIn(Set<String> validSet) {
        if (!containedIn(validSet)) {
            for (String param : params) {
                if (!validSet.contains(param)) {
                    errors.add("Parameter '" + param + "' is not in the valid set.");
                }
            }
        }
        return this;
    }

    public boolean containedIn(Set<String> validSet) {
        for (String param : params) {
            if (!validSet.contains(param)) {
                return false;
            }
        }
        return true;
    }

    // 检查参数内容是否包含于给定列表
    public ParamValidator checkContainedIn(List<String> validList) {
        return checkContainedIn(new HashSet<>(validList));
    }

    public boolean containedIn(List<String> validList) {
        return containedIn(new HashSet<>(validList));
    }

    // 检查参数内容是否包含于给定数组
    public ParamValidator checkContainedIn(String... validList) {
        return checkContainedIn(Arrays.asList(validList));
    }

    public boolean containedIn(String... validList) {
        return containedIn(Arrays.asList(validList));
    }

    // 新增：检查参数列表是否包含给定列表
    public ParamValidator checkContains(List<String> requiredList) {
        if (!contains(requiredList)) {
            errors.add("Parameters must contain all elements of the required list: " + requiredList);
        }
        return this;
    }

    public boolean contains(List<String> requiredList) {
        Set<String> paramSet = new HashSet<>(Arrays.asList(params));
        return paramSet.containsAll(requiredList);
    }

    // 新增：检查参数列表是否包含给定数组
    public ParamValidator checkContains(String... requiredArray) {
        return checkContains(Arrays.asList(requiredArray));
    }

    public boolean contains(String... requiredArray) {
        return contains(Arrays.asList(requiredArray));
    }

    // 获取验证结果
    public ValidationResult getValidationResult() {
        return new ValidationResult(errors.isEmpty(), errors);
    }

    // 返回验证后的参数（可选）
    public String[] getParams() {
        return params;
    }

    // 验证结果类
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        @Override
        public String toString() {
            if (valid) {
                return "Validation passed.";
            } else {
                return "Validation failed with errors: " + String.join(", ", errors);
            }
        }
    }

    public static void main(String[] args) {
        // 示例用法
        String[] params = {"param1", "param2", "param3", "param2"};

        // 使用 Collections.unmodifiableSet 创建不可变集合
        Set<String> validSet = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("param1", "param2", "param3", "param4")));

        ParamValidator validator = ParamValidator.create(params)
                .checkNotEmpty()
                .checkNoDuplicates()
                .checkSizeEquals(3)
                .checkContainedIn(validSet)
                .checkContains("param1", "param2");

        ValidationResult result = validator.getValidationResult();

        if (result.isValid()) {
            System.out.println("All validations passed!");
        } else {
            System.out.println(result);
        }

        // 仅判断而不记录错误
        System.out.println("Is no duplicates: " + validator.noDuplicates());
        System.out.println("Is size equals 3: " + validator.sizeEquals(3));
        System.out.println("Is contained in valid set: " + validator.containedIn(validSet));
        System.out.println("Contains required params: " + validator.contains("param1", "param2"));
    }
}