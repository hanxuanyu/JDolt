package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.core.DoltProcedure;
import com.hxuanyu.jdolt.core.DoltRepository;
import com.hxuanyu.jdolt.model.ProcedureResult;

/**
 * 添加或移除已配置的备份，与已配置的备份同步，将备份同步到远程URL，将远程URL备份恢复为一个新数据库。
 * TODO 待搭建完整的dolthub服务后再进行验证测试
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltBackup extends DoltRepository implements DoltProcedure {
    private static DoltBackup instance;


    private DoltBackup(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }


    public static synchronized DoltBackup instance(DoltConnectionManager connectionManager) {
        if (instance == null) {
            instance = new DoltBackup(connectionManager);
        }
        return instance;
    }

    @Override
    public <T> ProcedureResult<T> call(Class<T> resultClass, String... params) {
        return null;
    }

    @Override
    public boolean call(String... params) {
        return false;
    }
}
