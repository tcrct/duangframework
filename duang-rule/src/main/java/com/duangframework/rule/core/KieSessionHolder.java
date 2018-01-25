package com.duangframework.rule.core;
// KieSessionHolder.java
// Created by kensei on 2018/1/20.
// Copyright © 2018年 sythealth. All rights reserved.

import com.duangframework.core.kit.PathKit;
import com.duangframework.core.kit.ToolsKit;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author kensei
 * @version 1.0
 * @since 6.4.3
 * Create on 2018/1/20.
 */
public class KieSessionHolder {

    private static Logger logger = LoggerFactory.getLogger(KieSessionHolder.class);

    private static KieContainer kieContainer = null;
    private String ruleDir;

    public static final class Builder {
            String rulesDir = "/rules";
            public Builder ruleDir(String rulesDir) {
                this.rulesDir = rulesDir;
                return this;
            }
            public KieSessionHolder builder() {
                return new KieSessionHolder(this);
            }
        }

    public KieSessionHolder(Builder builder) {
        ruleDir = builder.rulesDir;
        init();
    }

    private void init() {
        try {
            kieContainer = loadKieContainer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private KieServices getKieServices() {
        return KieServices.Factory.get();
    }

    private File[] getRuleFiles() throws IOException {
        URL rootPathUrl = PathKit.duang().resource(ruleDir).path();
        File ruleFileDir = new File(rootPathUrl.getPath());
        if(!ruleFileDir.exists()) {
            ruleFileDir.mkdir();
            logger.warn("drools file dir ["+ ruleFileDir.getAbsolutePath() + "] is not exites! create it...");
        }
        File[] files = ruleFileDir.listFiles();
        return files;
    }

    private KieFileSystem kieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
        // 读取规则文件
        File[] files = getRuleFiles();
        ruleDir = ruleDir.startsWith("/") ? ruleDir.substring(1, ruleDir.length()) : ruleDir;
        for (File file : files) {
            kieFileSystem.write(ResourceFactory.newClassPathResource(ruleDir+"/"+file.getName(), "UTF-8"));
        }
        return kieFileSystem;
    }

    private KieContainer loadKieContainer() throws IOException {
        final KieServices ks = getKieServices();
        final KieRepository kr = ks.getRepository();
        final KieBuilder kb = ks.newKieBuilder(kieFileSystem());

        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors: " + kb.getResults().toString());
        }
        final KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        logger.warn("init kie container success");
        return kContainer;
    }

    public KieSession kieSession() {
        if(ToolsKit.isEmpty(kieContainer)){
            init();
        }
        return kieContainer.newKieSession();
    }
}
