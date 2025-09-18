package com.hxuanyu.jdolt.model;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户端统一返回报文实体类
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class SqlExecuteResult {
    /**
     * 成功状态码
     */
    public static final Integer CALL_STATUS_SUCCESS = 0;
    /**
     * 失败状态码
     */
    public static final Integer CALL_STATUS_FAILED = 1;
    /**
     * 返回码，用于标识是否成功
     */
    private Integer status;
    /**
     * 返回信息，用于描述调用或请求状态，如果成功则添加成功的信息，若失败须写明失败原因
     */
    private String msg;
    /**
     * 调用或请求的实际结果，如果请求失败，该值应为空
     */
    private List<Map<String, Object>> data;

    // ... 原有的构造器和静态方法保持不变 ...

    // ================= 数据获取便利方法 =================

    /**
     * 获取指定行指定列的原始数据
     *
     * @param rowIndex    行索引（从0开始）
     * @param columnName  列名
     * @return Optional包装的数据，如果不存在或执行失败则返回empty
     */
    public Optional<Object> getValue(int rowIndex, String columnName) {
        if (!isSuccess() || data == null || rowIndex < 0 || rowIndex >= data.size()) {
            return Optional.empty();
        }

        Map<String, Object> row = data.get(rowIndex);
        return Optional.ofNullable(row.get(columnName));
    }

    /**
     * 获取第一行指定列的原始数据
     *
     * @param columnName 列名
     * @return Optional包装的数据
     */
    public Optional<Object> getFirstValue(String columnName) {
        return getValue(0, columnName);
    }

    /**
     * 获取指定行指定列的字符串数据
     *
     * @param rowIndex   行索引
     * @param columnName 列名
     * @return 字符串数据，如果不存在则返回null
     */
    public String getString(int rowIndex, String columnName) {
        return getValue(rowIndex, columnName)
                .map(Object::toString)
                .orElse(null);
    }

    /**
     * 获取第一行指定列的字符串数据
     *
     * @param columnName 列名
     * @return 字符串数据，如果不存在则返回null
     */
    public String getString(String columnName) {
        return getString(0, columnName);
    }

    /**
     * 获取指定行指定列的字符串数据，如果为null则返回默认值
     *
     * @param rowIndex     行索引
     * @param columnName   列名
     * @param defaultValue 默认值
     * @return 字符串数据或默认值
     */
    public String getString(int rowIndex, String columnName, String defaultValue) {
        String result = getString(rowIndex, columnName);
        return result != null ? result : defaultValue;
    }

    /**
     * 获取第一行指定列的字符串数据，如果为null则返回默认值
     *
     * @param columnName   列名
     * @param defaultValue 默认值
     * @return 字符串数据或默认值
     */
    public String getString(String columnName, String defaultValue) {
        return getString(0, columnName, defaultValue);
    }

    /**
     * 获取指定行指定列的整数数据
     *
     * @param rowIndex   行索引
     * @param columnName 列名
     * @return Optional包装的整数数据
     */
    public Optional<Integer> getInt(int rowIndex, String columnName) {
        return getValue(rowIndex, columnName).map(obj -> {
            if (obj instanceof Integer) {
                return (Integer) obj;
            } else if (obj instanceof Number) {
                return ((Number) obj).intValue();
            } else {
                try {
                    return Integer.valueOf(obj.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        });
    }

    /**
     * 获取第一行指定列的整数数据
     *
     * @param columnName 列名
     * @return Optional包装的整数数据
     */
    public Optional<Integer> getInt(String columnName) {
        return getInt(0, columnName);
    }

    /**
     * 获取指定行指定列的整数数据，如果无法获取则返回默认值
     *
     * @param rowIndex     行索引
     * @param columnName   列名
     * @param defaultValue 默认值
     * @return 整数数据或默认值
     */
    public Integer getInt(int rowIndex, String columnName, Integer defaultValue) {
        return getInt(rowIndex, columnName).orElse(defaultValue);
    }

    /**
     * 获取第一行指定列的整数数据，如果无法获取则返回默认值
     *
     * @param columnName   列名
     * @param defaultValue 默认值
     * @return 整数数据或默认值
     */
    public Integer getInt(String columnName, Integer defaultValue) {
        return getInt(0, columnName, defaultValue);
    }

    /**
     * 获取指定行指定列的长整数数据
     *
     * @param rowIndex   行索引
     * @param columnName 列名
     * @return Optional包装的长整数数据
     */
    public Optional<Long> getLong(int rowIndex, String columnName) {
        return getValue(rowIndex, columnName).map(obj -> {
            if (obj instanceof Long) {
                return (Long) obj;
            } else if (obj instanceof Number) {
                return ((Number) obj).longValue();
            } else {
                try {
                    return Long.valueOf(obj.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        });
    }

    /**
     * 获取第一行指定列的长整数数据
     *
     * @param columnName 列名
     * @return Optional包装的长整数数据
     */
    public Optional<Long> getLong(String columnName) {
        return getLong(0, columnName);
    }

    /**
     * 获取指定行指定列的长整数数据，如果无法获取则返回默认值
     *
     * @param rowIndex     行索引
     * @param columnName   列名
     * @param defaultValue 默认值
     * @return 长整数数据或默认值
     */
    public Long getLong(int rowIndex, String columnName, Long defaultValue) {
        return getLong(rowIndex, columnName).orElse(defaultValue);
    }

    /**
     * 获取第一行指定列的长整数数据，如果无法获取则返回默认值
     *
     * @param columnName   列名
     * @param defaultValue 默认值
     * @return 长整数数据或默认值
     */
    public Long getLong(String columnName, Long defaultValue) {
        return getLong(0, columnName, defaultValue);
    }

    /**
     * 获取指定行指定列的双精度浮点数据
     *
     * @param rowIndex   行索引
     * @param columnName 列名
     * @return Optional包装的双精度浮点数据
     */
    public Optional<Double> getDouble(int rowIndex, String columnName) {
        return getValue(rowIndex, columnName).map(obj -> {
            if (obj instanceof Double) {
                return (Double) obj;
            } else if (obj instanceof Number) {
                return ((Number) obj).doubleValue();
            } else {
                try {
                    return Double.valueOf(obj.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        });
    }

    /**
     * 获取第一行指定列的双精度浮点数据
     *
     * @param columnName 列名
     * @return Optional包装的双精度浮点数据
     */
    public Optional<Double> getDouble(String columnName) {
        return getDouble(0, columnName);
    }

    /**
     * 获取指定行指定列的双精度浮点数据，如果无法获取则返回默认值
     *
     * @param rowIndex     行索引
     * @param columnName   列名
     * @param defaultValue 默认值
     * @return 双精度浮点数据或默认值
     */
    public Double getDouble(int rowIndex, String columnName, Double defaultValue) {
        return getDouble(rowIndex, columnName).orElse(defaultValue);
    }

    /**
     * 获取第一行指定列的双精度浮点数据，如果无法获取则返回默认值
     *
     * @param columnName   列名
     * @param defaultValue 默认值
     * @return 双精度浮点数据或默认值
     */
    public Double getDouble(String columnName, Double defaultValue) {
        return getDouble(0, columnName, defaultValue);
    }

    /**
     * 获取指定行指定列的BigDecimal数据
     *
     * @param rowIndex   行索引
     * @param columnName 列名
     * @return Optional包装的BigDecimal数据
     */
    public Optional<BigDecimal> getBigDecimal(int rowIndex, String columnName) {
        return getValue(rowIndex, columnName).map(obj -> {
            if (obj instanceof BigDecimal) {
                return (BigDecimal) obj;
            } else if (obj instanceof Number) {
                return BigDecimal.valueOf(((Number) obj).doubleValue());
            } else {
                try {
                    return new BigDecimal(obj.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        });
    }

    /**
     * 获取第一行指定列的BigDecimal数据
     *
     * @param columnName 列名
     * @return Optional包装的BigDecimal数据
     */
    public Optional<BigDecimal> getBigDecimal(String columnName) {
        return getBigDecimal(0, columnName);
    }

    /**
     * 获取指定行指定列的布尔数据
     *
     * @param rowIndex   行索引
     * @param columnName 列名
     * @return Optional包装的布尔数据
     */
    public Optional<Boolean> getBoolean(int rowIndex, String columnName) {
        return getValue(rowIndex, columnName).map(obj -> {
            if (obj instanceof Boolean) {
                return (Boolean) obj;
            } else if (obj instanceof Number) {
                return ((Number) obj).intValue() != 0;
            } else {
                String str = obj.toString().toLowerCase();
                return "true".equals(str) || "1".equals(str) || "yes".equals(str) || "y".equals(str);
            }
        });
    }

    /**
     * 获取第一行指定列的布尔数据
     *
     * @param columnName 列名
     * @return Optional包装的布尔数据
     */
    public Optional<Boolean> getBoolean(String columnName) {
        return getBoolean(0, columnName);
    }

    /**
     * 获取指定行指定列的布尔数据，如果无法获取则返回默认值
     *
     * @param rowIndex     行索引
     * @param columnName   列名
     * @param defaultValue 默认值
     * @return 布尔数据或默认值
     */
    public Boolean getBoolean(int rowIndex, String columnName, Boolean defaultValue) {
        return getBoolean(rowIndex, columnName).orElse(defaultValue);
    }

    /**
     * 获取第一行指定列的布尔数据，如果无法获取则返回默认值
     *
     * @param columnName   列名
     * @param defaultValue 默认值
     * @return 布尔数据或默认值
     */
    public Boolean getBoolean(String columnName, Boolean defaultValue) {
        return getBoolean(0, columnName, defaultValue);
    }

    /**
     * 获取指定行的所有数据
     *
     * @param rowIndex 行索引
     * @return 该行的数据Map，如果不存在则返回null
     */
    public Map<String, Object> getRow(int rowIndex) {
        if (!isSuccess() || data == null || rowIndex < 0 || rowIndex >= data.size()) {
            return null;
        }
        return data.get(rowIndex);
    }

    /**
     * 获取第一行的所有数据
     *
     * @return 第一行的数据Map，如果不存在则返回null
     */
    public Map<String, Object> getFirstRow() {
        return getRow(0);
    }

    /**
     * 获取指定列的所有数据
     *
     * @param columnName 列名
     * @return 该列的所有数据列表
     */
    public List<Object> getColumn(String columnName) {
        if (!isSuccess() || data == null) {
            return java.util.Collections.emptyList();
        }

        return data.stream()
                .map(row -> row.get(columnName))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定列的所有字符串数据
     *
     * @param columnName 列名
     * @return 该列的所有字符串数据列表
     */
    public List<String> getColumnAsString(String columnName) {
        return getColumn(columnName).stream()
                .map(obj -> obj == null ? null : obj.toString())
                .collect(Collectors.toList());
    }

    /**
     * 获取数据行数
     *
     * @return 数据行数，如果执行失败或数据为空则返回0
     */
    public int getRowCount() {
        if (!isSuccess() || data == null) {
            return 0;
        }
        return data.size();
    }

    /**
     * 获取数据列数（基于第一行数据）
     *
     * @return 数据列数，如果执行失败或数据为空则返回0
     */
    public int getColumnCount() {
        if (!isSuccess() || data == null || data.isEmpty()) {
            return 0;
        }
        return data.get(0).size();
    }

    /**
     * 获取所有列名（基于第一行数据）
     *
     * @return 列名列表，如果执行失败或数据为空则返回空列表
     */
    public List<String> getColumnNames() {
        if (!isSuccess() || data == null || data.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return new java.util.ArrayList<>(data.get(0).keySet());
    }

    /**
     * 检查是否包含指定列
     *
     * @param columnName 列名
     * @return 是否包含该列
     */
    public boolean hasColumn(String columnName) {
        if (!isSuccess() || data == null || data.isEmpty()) {
            return false;
        }
        return data.get(0).containsKey(columnName);
    }

    /**
     * 检查数据是否为空
     *
     * @return 数据是否为空
     */
    public boolean isEmpty() {
        return !isSuccess() || data == null || data.isEmpty();
    }

    /**
     * 检查数据是否不为空
     *
     * @return 数据是否不为空
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    // 在 SqlExecuteResult 类中添加以下方法：

    /**
     * 将查询结果转换为指定类型的对象列表
     *
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 转换后的对象列表
     */
    public <T> List<T> toObjectList(Class<T> clazz) {
        if (!isSuccess() || data == null || data.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> resultList = new ArrayList<>();

        try {
            for (Map<String, Object> row : data) {
                T instance = mapToObject(row, clazz);
                if (instance != null) {
                    resultList.add(instance);
                }
            }
        } catch (Exception e) {
            System.err.println("转换对象时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }

        return resultList;
    }

    /**
     * 将第一行查询结果转换为指定类型的对象
     *
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 转换后的对象，如果转换失败或没有数据则返回null
     */
    public <T> T toObject(Class<T> clazz) {
        List<T> list = toObjectList(clazz);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 将Map数据转换为指定类型的对象
     *
     * @param row   数据行
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 转换后的对象
     * @throws Exception 转换异常
     */
    private <T> T mapToObject(Map<String, Object> row, Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();

        // 获取类的所有字段（包括父类字段）
        List<Field> allFields = getAllFields(clazz);

        for (Field field : allFields) {
            String fieldName = field.getName();

            // 跳过静态字段和final字段
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
                    java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            // 尝试从row中获取对应的值
            Object value = findValueByFieldName(row, fieldName);

            if (value != null) {
                setFieldValue(instance, field, value);
            }
        }

        return instance;
    }

    /**
     * 获取类的所有字段（包括父类字段）
     *
     * @param clazz 类
     * @return 字段列表
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;

        while (currentClass != null) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }

    /**
     * 根据字段名从row中查找对应的值
     * 支持驼峰命名和下划线命名的转换
     *
     * @param row       数据行
     * @param fieldName 字段名
     * @return 对应的值
     */
    private Object findValueByFieldName(Map<String, Object> row, String fieldName) {
        // 直接匹配
        if (row.containsKey(fieldName)) {
            return row.get(fieldName);
        }

        // 驼峰转下划线匹配
        String underscoreName = camelToUnderscore(fieldName);
        if (row.containsKey(underscoreName)) {
            return row.get(underscoreName);
        }

        // 下划线转驼峰匹配
        String camelName = underscoreToCamel(fieldName);
        if (row.containsKey(camelName)) {
            return row.get(camelName);
        }

        // 忽略大小写匹配
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(fieldName) ||
                    entry.getKey().equalsIgnoreCase(underscoreName) ||
                    entry.getKey().equalsIgnoreCase(camelName)) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * 驼峰命名转下划线命名
     *
     * @param camelCase 驼峰命名字符串
     * @return 下划线命名字符串
     */
    private String camelToUnderscore(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * 下划线命名转驼峰命名
     *
     * @param underscore 下划线命名字符串
     * @return 驼峰命名字符串
     */
    private String underscoreToCamel(String underscore) {
        StringBuilder result = new StringBuilder();
        String[] parts = underscore.split("_");

        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                result.append(parts[i].toLowerCase());
            } else {
                result.append(parts[i].substring(0, 1).toUpperCase())
                        .append(parts[i].substring(1).toLowerCase());
            }
        }

        return result.toString();
    }

    /**
     * 为字段设置值，支持类型转换
     *
     * @param instance 对象实例
     * @param field    字段
     * @param value    值
     * @throws Exception 设置异常
     */
    private void setFieldValue(Object instance, Field field, Object value) throws Exception {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();

        // 如果值为null，直接设置
        if (value == null) {
            field.set(instance, null);
            return;
        }

        // 如果类型匹配，直接设置
        if (fieldType.isAssignableFrom(value.getClass())) {
            field.set(instance, value);
            return;
        }

        // 类型转换
        Object convertedValue = convertValue(value, fieldType);
        field.set(instance, convertedValue);
    }

    /**
     * 值类型转换
     *
     * @param value      原始值
     * @param targetType 目标类型
     * @return 转换后的值
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        String stringValue = value.toString();

        try {
            // String类型
            if (targetType == String.class) {
                return stringValue;
            }

            // 基本类型和包装类型
            if (targetType == int.class || targetType == Integer.class) {
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                return Integer.valueOf(stringValue);
            }

            if (targetType == long.class || targetType == Long.class) {
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
                return Long.valueOf(stringValue);
            }

            if (targetType == double.class || targetType == Double.class) {
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                return Double.valueOf(stringValue);
            }

            if (targetType == float.class || targetType == Float.class) {
                if (value instanceof Number) {
                    return ((Number) value).floatValue();
                }
                return Float.valueOf(stringValue);
            }

            if (targetType == boolean.class || targetType == Boolean.class) {
                if (value instanceof Boolean) {
                    return value;
                }
                if (value instanceof Number) {
                    return ((Number) value).intValue() != 0;
                }
                return Boolean.valueOf(stringValue) || "1".equals(stringValue) ||
                        "yes".equalsIgnoreCase(stringValue) || "y".equalsIgnoreCase(stringValue);
            }

            if (targetType == short.class || targetType == Short.class) {
                if (value instanceof Number) {
                    return ((Number) value).shortValue();
                }
                return Short.valueOf(stringValue);
            }

            if (targetType == byte.class || targetType == Byte.class) {
                if (value instanceof Number) {
                    return ((Number) value).byteValue();
                }
                return Byte.valueOf(stringValue);
            }

            // BigDecimal类型
            if (targetType == BigDecimal.class) {
                if (value instanceof BigDecimal) {
                    return value;
                }
                if (value instanceof Number) {
                    return BigDecimal.valueOf(((Number) value).doubleValue());
                }
                return new BigDecimal(stringValue);
            }

            // LocalDateTime类型
            if (targetType == LocalDateTime.class) {
                if (value instanceof LocalDateTime) {
                    return value;
                }
                // 尝试解析常见的日期时间格式
                return parseDateTime(stringValue);
            }

            // 枚举类型
            if (targetType.isEnum()) {
                return Enum.valueOf((Class<Enum>) targetType, stringValue);
            }

            // 默认返回原值
            return value;

        } catch (Exception e) {
            System.err.println("类型转换失败: " + value + " -> " + targetType.getSimpleName() + ", " + e.getMessage());
            return null;
        }
    }

    /**
     * 解析日期时间字符串
     *
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime对象
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        // 常见的日期时间格式
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(dateTimeStr, formatter);
            } catch (Exception e) {
                // 继续尝试下一个格式
            }
        }

        throw new RuntimeException("无法解析日期时间字符串: " + dateTimeStr);
    }

    // ... 原有的其他方法保持不变 ...

    /**
     * 构造器
     */
    private SqlExecuteResult() {
    }

    /**
     * 全参构造器
     */
    private SqlExecuteResult(Integer status, String msg, List<Map<String, Object>> data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造成功消息，如果本次返回需要返回数据，则调用该方法并将数据传入
     */
    public static SqlExecuteResult success(String msg, List<Map<String, Object>> data) {
        return new SqlExecuteResult(SqlExecuteResult.CALL_STATUS_SUCCESS, msg, data);
    }

    /**
     * 构造成功消息，本方法适用于不需要数据返回的情况
     */
    public static SqlExecuteResult success(String msg) {
        return success(msg, null);
    }

    /**
     * 构造简单的成功消息，使用默认的msg
     */
    public static SqlExecuteResult success() {
        return success("成功");
    }

    /**
     * 构造失败消息，由于请求失败，调用方无法获得正确的数据，因此该类型消息的数据区为null，但是msg中必须注明失败原因，
     * 失败原因由被调用方分析并设置
     *
     * @return
     */
    public static SqlExecuteResult failed(String msg) {
        return new SqlExecuteResult(SqlExecuteResult.CALL_STATUS_FAILED, msg, null);
    }

    /**
     * 构造带有默认msg字段的失败消息
     */
    public static SqlExecuteResult failed() {
        return failed("失败");
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ProcedureResult{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public boolean isSuccess() {
        return this.status.equals(CALL_STATUS_SUCCESS);
    }

    /**
     * 输出SQL执行结果到控制台，以表格形式展示
     */
    public void print() {
        if (!isSuccess()) {
            System.out.println("执行失败: " + this.msg);
            return;
        }

        if (data == null || data.isEmpty()) {
            System.out.println("执行成功，数据为空");
            return;
        }

        // 获取所有列名
        List<String> columnNames = new java.util.ArrayList<>(data.get(0).keySet());

        // 计算每列的最大宽度
        Map<String, Integer> columnWidths = new java.util.HashMap<>();
        for (String column : columnNames) {
            columnWidths.put(column, column.length());
        }

        for (Map<String, Object> row : data) {
            for (String column : columnNames) {
                Object value = row.get(column);
                String valueStr = value == null ? "NULL" : value.toString();
                columnWidths.put(column, Math.max(columnWidths.get(column), valueStr.length()));
            }
        }

        // 打印表头
        StringBuilder headerLine = new StringBuilder();
        StringBuilder separator = new StringBuilder();

        for (String column : columnNames) {
            int width = columnWidths.get(column);
            headerLine.append(String.format("| %-" + width + "s ", column));
            separator.append("+-");
            for (int i = 0; i < width; i++) {
                separator.append("-");
            }
            separator.append("-");
        }
        headerLine.append("|");
        separator.append("+");

        System.out.println(separator);
        System.out.println(headerLine);
        System.out.println(separator);

        // 打印数据行
        for (Map<String, Object> row : data) {
            StringBuilder dataLine = new StringBuilder();
            for (String column : columnNames) {
                Object value = row.get(column);
                String valueStr = value == null ? "NULL" : value.toString();
                dataLine.append(String.format("| %-" + columnWidths.get(column) + "s ", valueStr));
            }
            dataLine.append("|");
            System.out.println(dataLine);
        }

        System.out.println(separator);
        System.out.println("共计 " + data.size() + " 条记录");
    }

    /**
     * 输出SQL执行结果到控制台，以CSV格式展示
     *
     * @param delimiter 分隔符
     */
    public void print(String delimiter) {
        if (!isSuccess()) {
            System.out.println("执行失败: " + this.msg);
            return;
        }

        if (data == null || data.isEmpty()) {
            System.out.println("执行成功，但没有返回数据");
            return;
        }

        // 获取所有列名
        List<String> columnNames = new java.util.ArrayList<>(data.get(0).keySet());

        // 打印表头
        System.out.println(String.join(delimiter, columnNames));

        // 打印数据行
        for (Map<String, Object> row : data) {
            StringBuilder dataLine = new StringBuilder();
            for (int i = 0; i < columnNames.size(); i++) {
                Object value = row.get(columnNames.get(i));
                String valueStr = value == null ? "" : value.toString();
                dataLine.append(valueStr);
                if (i < columnNames.size() - 1) {
                    dataLine.append(delimiter);
                }
            }
            System.out.println(dataLine);
        }

        System.out.println("共计 " + data.size() + " 条记录");
    }

    /**
     * 输出SQL执行结果到控制台，以CSV格式展示，使用逗号作为分隔符
     */
    public void printCsv() {
        print(",");
    }

    /**
     * 输出SQL执行结果到控制台，以CSV格式展示，使用制表符作为分隔符
     */
    public void printTsv() {
        print("\t");
    }

    /**
     * 输出SQL执行结果到控制台，使用自定义格式
     *
     * @param useHeader 是否显示表头
     * @param delimiter 分隔符
     * @param showCount 是否显示记录总数
     */
    public void print(boolean useHeader, String delimiter, boolean showCount) {
        if (!isSuccess()) {
            System.out.println("执行失败: " + this.msg);
            return;
        }

        if (data == null || data.isEmpty()) {
            System.out.println("执行成功，但没有返回数据");
            return;
        }

        // 获取所有列名
        List<String> columnNames = new java.util.ArrayList<>(data.get(0).keySet());

        // 打印表头
        if (useHeader) {
            System.out.println(String.join(delimiter, columnNames));
        }

        // 打印数据行
        for (Map<String, Object> row : data) {
            StringBuilder dataLine = new StringBuilder();
            for (int i = 0; i < columnNames.size(); i++) {
                Object value = row.get(columnNames.get(i));
                String valueStr = value == null ? "" : value.toString();
                dataLine.append(valueStr);
                if (i < columnNames.size() - 1) {
                    dataLine.append(delimiter);
                }
            }
            System.out.println(dataLine);
        }

        if (showCount) {
            System.out.println("共计 " + data.size() + " 条记录");
        }
    }

    /**
     * 将SQL执行结果转换为JSON字符串
     *
     * @return JSON格式的结果字符串
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        // 添加status字段
        json.append("\"status\":").append(status).append(",");
        
        // 添加msg字段
        json.append("\"msg\":\"").append(escapeJsonString(msg)).append("\",");
        
        // 添加data字段
        json.append("\"data\":");
        if (data == null) {
            json.append("null");
        } else {
            json.append("[");
            for (int i = 0; i < data.size(); i++) {
                if (i > 0) {
                    json.append(",");
                }
                Map<String, Object> row = data.get(i);
                json.append("{");
                boolean first = true;
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    if (!first) {
                        json.append(",");
                    }
                    first = false;
                    json.append("\"").append(escapeJsonString(entry.getKey())).append("\":");
                    Object value = entry.getValue();
                    if (value == null) {
                        json.append("null");
                    } else if (value instanceof String) {
                        json.append("\"").append(escapeJsonString(value.toString())).append("\"");
                    } else if (value instanceof Number || value instanceof Boolean) {
                        json.append(value.toString());
                    } else {
                        json.append("\"").append(escapeJsonString(value.toString())).append("\"");
                    }
                }
                json.append("}");
            }
            json.append("]");
        }
        
        json.append("}");
        return json.toString();
    }

    /**
     * 输出SQL执行结果到控制台，以JSON格式展示
     */
    public void printJson() {
        System.out.println(toJson());
    }

    /**
     * 转义JSON字符串中的特殊字符
     *
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    private String escapeJsonString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
