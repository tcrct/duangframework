//package com.duangframework.config.client;
//
//
//import com.duangframework.config.apollo.model.ApolloModel;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * @author Created by laotang
// * @date createed in 2018/3/6.
// */
//public class ConfigClientFactory {
//
//    private static ApolloModel apolloModel;
//
//    public static class Builder {
//        private String env;
//        private String appId;
//        private String metaUrl;
//        private String[] nameSpaces;
//
//        public Builder() {
//        }
//
//        public Builder(String env) {
//            this.env = env;
//        }
//
//        public Builder appId(String appId) {
//            this.appId = appId;
//            return this;
//        }
//        public Builder env(String env) {
//            this.env = env;
//            return this;
//        }
//        public Builder metaUrl(String metaUrl) {
//            this.metaUrl = metaUrl;
//            return this;
//        }
//        public Builder nameSpaces(String[] nameSpaces) {
//            this.nameSpaces = nameSpaces;
//            return this;
//        }
//
//        public ConfigClientFactory builder() {
//            return new ConfigClientFactory(this);
//        }
//    }
//
////    public static final class LOCAL extends Builder {
////        public LOCAL() {
////            super("dev");
////        }
////    }
////
////    public static final class OBT extends Builder {
////        public OBT() {
////            super("uat");
////        }
////    }
////
////    public static final class API extends Builder {
////        public API() {
////            super("pro");
////        }
////    }
//
//    public ConfigClientFactory() {
//
//    }
//
//    public ConfigClientFactory(Builder builder) {
//        List<String> nameSpaceList = new ArrayList<>();
//        if(null != builder.nameSpaces) {
//            nameSpaceList = Arrays.asList(builder.nameSpaces);
//        }
//        apolloModel = new ApolloModel(builder.appId, nameSpaceList, builder.env, builder.metaUrl);
//    }
//
//    public ConfigClient getClient() {
////        return new ConfigClient(apolloModel);
//        return null;
//    }
//
//}
