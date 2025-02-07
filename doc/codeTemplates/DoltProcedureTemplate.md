### 文件模板

#### DoltProcedure

```java
package com.hxuanyu.jdolt.core.procedure;


import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.model.ProcedureResult;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;

public class DoltCherryPick extends DoltRepository implements DoltProcedure<DoltCherryPick.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCherryPick> INSTANCES = new ConcurrentHashMap<>();

    private DoltCherryPick(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCherryPick getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCherryPick(connectionManager));
    }

    public static class Params extends AbstractParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_cherry_pick"), params);
    }

}

```

### LiveTemplate

doltParamMethod:

```java

@MethodMutexGroup({"$METHOD_NAME$"})
public Params $METHOD_NAME$($PARAMS$) {
    validator.checkAndMark("$METHOD_NAME$");
    addFlags("$FLAGS$");
    return this;
}
```

