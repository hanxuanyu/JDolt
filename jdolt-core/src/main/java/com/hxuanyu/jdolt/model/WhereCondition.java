package com.hxuanyu.jdolt.model;

/**
 * 表示WHERE条件的类，包含列名、比较操作符和值
 */
public class WhereCondition {
    
    /**
     * 比较操作符枚举
     */
    public enum Operator {
        EQUALS("="),
        NOT_EQUALS("!="),
        NOT_EQUALS_ALT("<>"),
        LIKE("LIKE"),
        NOT_LIKE("NOT LIKE"),
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUAL(">="),
        LESS_THAN("<"),
        LESS_THAN_OR_EQUAL("<="),
        IN("IN"),
        NOT_IN("NOT IN"),
        IS_NULL("IS NULL"),
        IS_NOT_NULL("IS NOT NULL");
        
        private final String symbol;
        
        Operator(String symbol) {
            this.symbol = symbol;
        }
        
        public String getSymbol() {
            return symbol;
        }
    }
    
    private final String column;
    private final Operator operator;
    private final Object value;
    
    /**
     * 构造WHERE条件
     * 
     * @param column 列名
     * @param operator 比较操作符
     * @param value 值
     */
    public WhereCondition(String column, Operator operator, Object value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }
    
    /**
     * 创建等于条件的便捷方法
     */
    public static WhereCondition equals(String column, Object value) {
        return new WhereCondition(column, Operator.EQUALS, value);
    }
    
    /**
     * 创建不等于条件的便捷方法
     */
    public static WhereCondition notEquals(String column, Object value) {
        return new WhereCondition(column, Operator.NOT_EQUALS, value);
    }
    
    /**
     * 创建LIKE条件的便捷方法
     */
    public static WhereCondition like(String column, String pattern) {
        return new WhereCondition(column, Operator.LIKE, pattern);
    }
    
    /**
     * 创建NOT LIKE条件的便捷方法
     */
    public static WhereCondition notLike(String column, String pattern) {
        return new WhereCondition(column, Operator.NOT_LIKE, pattern);
    }
    
    /**
     * 创建大于条件的便捷方法
     */
    public static WhereCondition greaterThan(String column, Object value) {
        return new WhereCondition(column, Operator.GREATER_THAN, value);
    }
    
    /**
     * 创建大于等于条件的便捷方法
     */
    public static WhereCondition greaterThanOrEqual(String column, Object value) {
        return new WhereCondition(column, Operator.GREATER_THAN_OR_EQUAL, value);
    }
    
    /**
     * 创建小于条件的便捷方法
     */
    public static WhereCondition lessThan(String column, Object value) {
        return new WhereCondition(column, Operator.LESS_THAN, value);
    }
    
    /**
     * 创建小于等于条件的便捷方法
     */
    public static WhereCondition lessThanOrEqual(String column, Object value) {
        return new WhereCondition(column, Operator.LESS_THAN_OR_EQUAL, value);
    }
    
    /**
     * 创建IS NULL条件的便捷方法
     */
    public static WhereCondition isNull(String column) {
        return new WhereCondition(column, Operator.IS_NULL, null);
    }
    
    /**
     * 创建IS NOT NULL条件的便捷方法
     */
    public static WhereCondition isNotNull(String column) {
        return new WhereCondition(column, Operator.IS_NOT_NULL, null);
    }
    
    public String getColumn() {
        return column;
    }
    
    public Operator getOperator() {
        return operator;
    }
    
    public Object getValue() {
        return value;
    }
    
    /**
     * 检查是否需要参数值（IS NULL和IS NOT NULL不需要参数）
     */
    public boolean needsValue() {
        return operator != Operator.IS_NULL && operator != Operator.IS_NOT_NULL;
    }
    
    @Override
    public String toString() {
        return "WhereCondition{" +
                "column='" + column + '\'' +
                ", operator=" + operator +
                ", value=" + value +
                '}';
    }
}