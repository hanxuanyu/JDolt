### 文件模板

#### DoltProcedure

```text
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};

#end
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractProcedureParamBuilder;
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

    public static class Params extends AbstractParamBuilder<Params> {

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
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("${Procedure}"), params);
    }

}

```

### LiveTemplate

doltParamMethod:

```text

@MethodMutexGroup({"$METHOD_NAME$"})
public Params $METHOD_NAME$($PARAMS$) {
    validator.checkAndMark("$METHOD_NAME$");
    addFlags("$FLAGS$");
    return this;
}
```

