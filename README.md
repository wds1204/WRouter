# WRouter
WRouter: Android平台多模块开发的路由库：


**初始化**
```
WRouter.getInstance().init(this);
```
**在需要的模块中的gradle添加moduleName**
```groovy

 repositories {
        mavenCentral()
    }
    
 defaultConfig {
         
         javaCompileOptions {
             annotationProcessorOptions {
                  //每个模块的moduleName不能重复
                 arguments = [moduleName: 'member']
             }
         }
     }
```
**并在需要的模块中添加action**

```java

@Action(path = "member/path",threadMode=ThreadMode.MAIN)
public class MemberAction implements IRouterAction {
    @Override
    public RouterResult invokeAction(Context context, Map<String, Object> requestData) {

        //
        return null;
    }
}
```

**调用方式**

```java
// member/path 与添加的action注解中的path路径相同
WRouter.getInstance().action("member/path").context(context).invokeAction();

```