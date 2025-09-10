### 文件模板

#### DoltProcedure

```txt
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};

#end
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;

#parse("File Header.java")
public class ${NAME} extends DoltRepository implements DoltProcedure<${NAME}.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, ${NAME}> INSTANCES = new ConcurrentHashMap<>();

    private ${NAME}(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static ${NAME} getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new ${NAME}(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }
    
        doltParamMethod


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return SqlBuilder.callProcedure("${Procedure}")
                .withParams(params)
                .build();
    }

}
```

#### DoltInfoFunction

```txt
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};

#end
import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractInfoFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;

#parse("File Header.java")
public class ${NAME} extends DoltRepository implements DoltInfoFunction<${NAME}.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, ${NAME}> INSTANCES = new ConcurrentHashMap<>();

    private ${NAME}(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static ${NAME} getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new ${NAME}(connectionManager));
    }

    public static class Params extends AbstractInfoFunctionParamBuilder<Params> {

        protected Params(DoltInfoFunction<Params> doltInfoFunction) {
            super(Params.class, doltInfoFunction);
        }
  
        doltParamMethod


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.select()
                .fromFunction("${Function}")
                .withParams(params)
                .build();
    }

}
```

#### DoltTableFunction

```txt
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};

#end
import com.hxuanyu.jdolt.interfaces.DoltTableFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractTableFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;


import java.util.concurrent.ConcurrentHashMap;

#parse("File Header.java")
public class ${NAME} extends DoltRepository implements DoltTableFunction<${NAME}.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, ${NAME}> INSTANCES = new ConcurrentHashMap<>();

    private ${NAME}(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static ${NAME} getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new ${NAME}(connectionManager));
    }

    public static class Params extends AbstractTableFunctionParamBuilder<Params> {

        protected Params(DoltTableFunction<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }
  
        doltParamMethod


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.select()
                .fromFunction("${Function}")
                .withParams(params)
                .build();
    }

}
```

### DoltSystemTable

```txt
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};

#end
import com.hxuanyu.jdolt.interfaces.DoltSystemTable;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractSystemTableParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;


import java.util.concurrent.ConcurrentHashMap;

#parse("File Header.java")
public class ${NAME} extends DoltRepository implements DoltSystemTable<${NAME}.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, ${NAME}> INSTANCES = new ConcurrentHashMap<>();

    private ${NAME}(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static ${NAME} getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new ${NAME}(connectionManager));
    }

    public static class Params extends AbstractSystemTableParamBuilder<Params> {

        protected Params(DoltTableFunction<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }
  
        doltParamMethod


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.select()
                .from("${Table}")
                .withParams(params)
                .build();
    }

}
```

### 代码片段

#### `doltParamMethod`

```txt
@MethodMutexGroup({"$METHOD_NAME$"})
public Params $METHOD_NAME$($PARAMS$) {
    validator.checkAndMark("$METHOD_NAME$");
    addFlags("$FLAGS$");
    return this;
}
```