package com.sun.wrouter.compiler.processor;

import com.google.auto.service.AutoService;
import com.sun.wrouter.base.annotation.Action;
import com.sun.wrouter.compiler.utils.Consts;
import com.sun.wrouter.compiler.utils.TextUtils;

import java.lang.annotation.Annotation;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: BaseProcessor.java
 * Author: wds_sun
 * Date: 2019-10-21 19:10
 * Description:
 */
@AutoService(Processor.class)
public  abstract class BaseProcessor extends AbstractProcessor {
    protected final String KEY_MODULE_NAME = "moduleName";
    protected Filer mFiler;
    protected Elements mElementUtils;
    protected TypeMirror iRouterAction;

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

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();

    }



    protected boolean isNotEmpty(Map<String, String> options) {
        return options != null && !options.isEmpty();
    }


    protected void error(Element element, String message, String... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    protected void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }



    protected  abstract Set<Class<? extends Annotation>> getSupportedAnnotations();



}
