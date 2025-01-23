package com.hxuanyu.jdolt.util;

public class ArrayUtils {

    // 私有构造方法，防止实例化
    private ArrayUtils() {}

    // ==================== int[] 数组操作 ====================

    public static int[] add(int[] array, int element) {
        int[] newArray = java.util.Arrays.copyOf(array, array.length + 1);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static int[] merge(int[] array1, int[] array2) {
        int[] newArray = java.util.Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, newArray, array1.length, array2.length);
        return newArray;
    }

    public static int[] remove(int[] array, int index) {
        if (index < 0 || index >= array.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        int[] newArray = new int[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);
        return newArray;
    }

    public static int indexOf(int[] array, int element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(int[] array, int element) {
        return indexOf(array, element) != -1;
    }

    public static int[] reverse(int[] array) {
        int[] newArray = java.util.Arrays.copyOf(array, array.length);
        for (int i = 0, j = newArray.length - 1; i < j; i++, j--) {
            int temp = newArray[i];
            newArray[i] = newArray[j];
            newArray[j] = temp;
        }
        return newArray;
    }

    public static int[] subArray(int[] array, int start, int end) {
        if (start < 0 || end > array.length || start > end) {
            throw new IllegalArgumentException("Invalid start or end index");
        }
        return java.util.Arrays.copyOfRange(array, start, end);
    }

    public static void fill(int[] array, int value) {
        java.util.Arrays.fill(array, value);
    }

    public static void printArray(int[] array) {
        System.out.println(java.util.Arrays.toString(array));
    }

    // ==================== String[] 数组操作 ====================

    public static String[] add(String[] array, String element) {
        String[] newArray = java.util.Arrays.copyOf(array, array.length + 1);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static String[] merge(String[] array1, String[] array2) {
        String[] newArray = java.util.Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, newArray, array1.length, array2.length);
        return newArray;
    }

    public static String[] remove(String[] array, int index) {
        if (index < 0 || index >= array.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        String[] newArray = new String[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);
        return newArray;
    }

    public static int indexOf(String[] array, String element) {
        for (int i = 0; i < array.length; i++) {
            if ((array[i] == null && element == null) || (array[i] != null && array[i].equals(element))) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(String[] array, String element) {
        return indexOf(array, element) != -1;
    }

    public static String[] reverse(String[] array) {
        String[] newArray = java.util.Arrays.copyOf(array, array.length);
        for (int i = 0, j = newArray.length - 1; i < j; i++, j--) {
            String temp = newArray[i];
            newArray[i] = newArray[j];
            newArray[j] = temp;
        }
        return newArray;
    }

    public static String[] subArray(String[] array, int start, int end) {
        if (start < 0 || end > array.length || start > end) {
            throw new IllegalArgumentException("Invalid start or end index");
        }
        return java.util.Arrays.copyOfRange(array, start, end);
    }

    public static void fill(String[] array, String value) {
        java.util.Arrays.fill(array, value);
    }

    public static void printArray(String[] array) {
        System.out.println(java.util.Arrays.toString(array));
    }
}
