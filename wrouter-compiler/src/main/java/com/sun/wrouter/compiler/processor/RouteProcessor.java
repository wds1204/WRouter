package com.sun.wrouter.compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.wrouter.base.annotation.Action;
import com.sun.wrouter.compiler.utils.Consts;
import com.sun.wrouter.compiler.utils.TextUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: RouteProcessor.java
 * Author: wds_sun
 * Date: 2019-10-21 10:27
 * Description:
 */
@AutoService(Processor.class)
public class RouteProcessor extends AbstractProcessor {

    private final String KEY_MODULE_NAME = "moduleName";
    private Filer mFiler;
    private Elements mElementUtils;
    private TypeMirror iRouterAction;

    /**
     * 用来指定支持的 SourceVersion
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    /**
     * 给到需要处理的注解
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotations : getSupportedAnnotations()) {
            types.add(annotations.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(Action.class);
        return annotations;

    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();

        iRouterAction = mElementUtils.getTypeElement(Consts.ROUTERACTION).asType();


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
        ClassName routerAssistClassName = ClassName.get("com.sun.api.action", "IRouterModule");
        ClassName mapClassName = ClassName.get("java.util", "Map");
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("WRouter$$Module$$" + moduleName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addSuperinterface(routerAssistClassName)
                .addField(mapClassName, "actions", Modifier.PRIVATE);

        MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        constructorMethodBuilder.addStatement("actions = new $T<>()", ClassName.bestGuess("java.util.HashMap"));

        //解析所有的Action信息

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Action.class);

        HashMap<Object, Object> modules = new HashMap<>(elements.size());

        ClassName actionWrapper = ClassName.get("com.sun.api.extra", "ActionWrapper");
        ClassName threadModeClassName = ClassName.get("com.sun.wrouter.base", "ThreadMode");

        for (Element element : elements) {
            Action annotation = element.getAnnotation(Action.class);

            String path = annotation.path();

            if (!path.startsWith(moduleName + "/")) {
                error(element, "Path name of the action must begin with %s%s", moduleName, "/");
            }
            //获取Action的ClassName
            Element enclosingElement = element.getEnclosingElement();

            String packageName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
            String actionClassName = packageName + "." + element.getSimpleName();

            // 判断 Interceptor 注解类是否实现了 ActionInterceptor
            if (!((TypeElement) element).getInterfaces().contains(iRouterAction)) {
                error(element, "%s verify failed, @Action must be implements %s", element.getSimpleName().toString(), Consts.ROUTERACTION);
            }

            if (modules.containsKey(path)) {
                error(element, "Path name of the action must begin with %s%s", moduleName, "/");
            }

            modules.put(path,actionClassName);

            constructorMethodBuilder.addStatement("this.actions.put($S,$T.build($T.class, $S,"+annotation.extraProcess()+",$T."+annotation.threadMode()+"))",path,actionWrapper,ClassName.bestGuess(actionClassName),path,threadModeClassName);

        }
        MethodSpec.Builder findMethod = MethodSpec.methodBuilder("findAction")
                .addParameter(String.class, "actionName")
                .addAnnotation(Override.class)
                .returns(actionWrapper)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        findMethod.addStatement("return (ActionWrapper)actions.get(actionName)");


        classBuilder.addMethod(findMethod.build());
        classBuilder.addMethod(constructorMethodBuilder.build());
        try {
            JavaFile.builder(Consts.ROUTER_MODULE_PACK_NAME, classBuilder.build())
                    .addFileComment("WRouter 自动生成")
                    .build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Attempt to recreate a file for type
        return false;
    }


    private boolean isNotEmpty(Map<String, String> options) {
        return options != null && !options.isEmpty();
    }


    private void error(Element element, String message, String... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }

}
