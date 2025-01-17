package com.hxuanyu.jdolt.util;

/**
 * @author hanxuanyu
 * @version 1.0
 */
public class BranchNameValidator {

    /**
     * 校验分支名称是否有效
     *
     * @param branchName 分支名称
     * @return 分支名称合规时返回 true，否则返回 false
     */
    public static boolean isValidBranchName(String branchName) {
        // 1. 是否为空
        if (branchName == null || branchName.isEmpty()) {
            return false;
        }

        // 2. 不得以 '.' 开头
        if (branchName.startsWith(".")) {
            return false;
        }

        // 3. 不得包含 '..'
        if (branchName.contains("..")) {
            return false;
        }

        // 4. 不得包含 '@{'
        if (branchName.contains("@{")) {
            return false;
        }

        // 5. 不得以 '/' 结尾
        if (branchName.endsWith("/")) {
            return false;
        }

        // 6. 不得以 '.lock' 结尾
        if (branchName.endsWith(".lock")) {
            return false;
        }

        // 7. 检测是否为 32 个字符且均为 [0-9 or a-z]
        if (branchName.length() == 32 && branchName.matches("[0-9a-z]{32}")) {
            return false;
        }

        // 8. 验证所有字符是否为 ASCII，且不包含控制字符和不允许的字符
        for (char c : branchName.toCharArray()) {
            if (c > 127) { // 非 ASCII 字符
                return false;
            }
            if (c < 0x20 || c == 0x7F || c == ' ') { // 控制字符或空格
                return false;
            }
            switch (c) { // 不允许的字符
                case ':':
                case '?':
                case '[':
                case '\\':
                case '^':
                case '~':
                case '*':
                    return false;
                default:
                    break;
            }
        }

        // 若均未触发上述任何条件，则视为合规
        return true;
    }

    /**
     * 测试代码的主方法，对 isValidBranchName 方法提供单元测试
     */
    public static void main(String[] args) {
        // 定义测试用例
        String[] testCases = {
                null,                               // 测试空值
                "",                                 // 测试空字符串
                ".",                                // 测试以 "." 开头
                "..branch",                         // 测试包含 ".."
                "feature@{foo}",                    // 测试包含 "@{"
                "branch/",                          // 测试以 "/" 结尾
                "branch.lock",                      // 测试以 ".lock" 结尾
                "abcd1234abcd1234abcd1234abcd1234", // 测试提交哈希值形式
                "feature:fix",                      // 测试非法字符 ':'
                "feature?new",                      // 测试非法字符 '?'
                "feature[new]",                     // 测试非法字符 '['
                "feature\\name",                    // 测试非法字符 '\\'
                "feature^name",                     // 测试非法字符 '^'
                "feature~name",                     // 测试非法字符 '~'
                "feature*name",                     // 测试非法字符 '*'
                "feature name",                     // 测试包含空格
                "branch\nname",                     // 测试包含换行符
                "valid-branch_name",                // 合法分支名
                "ValidBranchName123",               // 合法分支名
                "v1.2.3/feature"                    // 合法分支名
        };

        // 遍历测试用例并打印结果
        for (String testCase : testCases) {
            boolean result = isValidBranchName(testCase);
            System.out.printf("Branch Name: '%s' -> %s%n", testCase, result ? "VALID" : "INVALID");
        }
    }
}
