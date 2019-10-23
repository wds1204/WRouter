package com.sun.wrouter.compiler.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.wrouter.base.annotation.Action;
import com.sun.wrouter.base.annotation.Interceptor;
import com.sun.wrouter.compiler.utils.Consts;
import com.sun.wrouter.compiler.utils.TextUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Copyright (C), 2016-2019
 * File: InterceptorProcessor.java
 * Author: wds_sun
 * Date: 2019-10-21 19:09
 * Description:
 */
public class InterceptorProcessor extends BaseProcessor {

    private TypeMirror iInterceptor;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        iInterceptor = mElementUtils.getTypeElement(Consts.ACTIONINTERCEPTOR).asType();

    }

    @Override
    protected Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(Interceptor.class);
        return annotations;

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        String moduleName = "";

        Map<String, String> options = processingEnv.getOptions();
        if (isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }
        System.out.println("moduleName ---->" + moduleName);

        if (!TextUtils.isEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
        } else {
            String errorMessage = "These no module name, at 'build.gradle', like :\n" +
                    "apt {\n" +
                    "    arguments {\n" +
                    "        moduleName project.getName();\n" +
                    "    }\n" +
                    "}\n";
            throw new RuntimeException("DRouter::Compiler >>> No module name, for more information, look at gradle log.\n" + errorMessage);

        }


        // 生成类继承和实现接口
        ClassName routerAssistClassName = ClassName.get("com.sun.api.action", "IRouterInterceptor");
        ClassName mapClassName = ClassName.get("java.util", "Map");
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("WRouter$$Interceptor$$" + moduleName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addSuperinterface(routerAssistClassName)
                .addField(mapClassName, "interceptors", Modifier.PRIVATE);


        // 构造函数
        MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        constructorMethodBuilder.addStatement("interceptors = new $T<>()", ClassName.get("java.util", "TreeMap"));

        // 2. 解析到所有的 Action 信息
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Interceptor.class);
        Map<Integer, String> interceptors = new HashMap<>(elements.size());

        for (Element element : elements) {

            // 获取注解上面的 priority
            Interceptor interceptorAnnotation = element.getAnnotation(Interceptor.class);
            int priority = interceptorAnnotation.priority();

            // 获取 Interceptor 的 ClassName
            Element enclosingElement = element.getEnclosingElement();
            String packageName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
            String interceptorClassName = packageName + "." + element.getSimpleName();

            // 判断 Interceptor 注解类是否实现了 ActionInterceptor
            if (!((TypeElement) element).getInterfaces().contains(iInterceptor)) {
                error(element, "%s verify failed, @Interceptor must be implements %s", element.getSimpleName().toString(), Consts.ACTIONINTERCEPTOR);
            }

            if (interceptors.containsKey(priority)) {
                // 输出错误，拦截器优先级 冲突重复了
                error(element, "More than one interceptors use same priority <%s> , The last interceptor was <%s>", String.valueOf(priority), interceptors.get(priority));
            }
            // 添加到集合
            interceptors.put(priority, interceptorClassName);

            constructorMethodBuilder.addStatement("this.interceptors.put(" + priority + ",new $T())", ClassName.bestGuess(interceptorClassName));
        }

        // 实现方法
        MethodSpec.Builder getInterceptors = MethodSpec.methodBuilder("getInterceptors")
                .addAnnotation(Override.class)
                .returns(List.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        getInterceptors.addStatement("return $T.getInterceptorClasses(interceptors)", ClassName.get("com.sun.api.utils", "MapUtils"));

        classBuilder.addMethod(constructorMethodBuilder.build());
        classBuilder.addMethod(getInterceptors.build());


        try {
            JavaFile.builder(Consts.ROUTER_INTERCEPTOR_PACK_NAME, classBuilder.build())
                    .addFileComment("自动生成拦截器")
                    .build().writeTo(mFiler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
