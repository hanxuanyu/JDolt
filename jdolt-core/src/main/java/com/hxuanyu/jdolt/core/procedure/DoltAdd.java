package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.constant.DoltSqlTemplate;
import com.hxuanyu.jdolt.core.DoltProcedure;
import com.hxuanyu.jdolt.core.DoltRepository;
import com.hxuanyu.jdolt.model.ProcedureResult;

/**
 * DoltAdd()相关操作
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltAdd extends DoltRepository implements DoltProcedure {

    private static DoltAdd instance;


    private DoltAdd(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }


    public static synchronized DoltAdd instance(DoltConnectionManager connectionManager) {
        if (instance == null) {
            instance = new DoltAdd(connectionManager);
        }
        return instance;
    }


    @Override
    public <T> ProcedureResult<T> call(Class<T> resultClass, String... params) {
        if (call(params)) {
            return ProcedureResult.success();
        } else {
            return ProcedureResult.failed();
        }
    }

    @Override
    public boolean call(String... params) {
        return commonDoltExecute(DoltSqlTemplate.buildAddSql(params), params);
    }



    /**
     * 将所有的表添加到暂存区
     *
     * @return 执行结果，true：执行成功，false：执行失败
     */
    public boolean addAll() {
        return call("-A");
    }



    /**
     * 将传入的table列表添加到暂存区
     *
     * @param tables 要添加到暂存区的表列表
     * @return 执行结果，true：执行成功，false：执行失败
     */
    public boolean addTables(String... tables) {
        return call(tables);
    }
}
