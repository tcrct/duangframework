package com.duangframework.core.common.classes;

import com.duangframework.core.common.Const;
import com.duangframework.core.kit.PathKit;
import com.duangframework.core.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描抽象类
 * @author laotang
 * @date 2017/11/12 0012
 */
public abstract class AbstractClassTemplate implements IClassTemplate {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClassTemplate.class);

    protected Set<Class<? extends Annotation>> annotationSet = new HashSet<>();
    protected Set<String> packageSet = new HashSet<>();
    protected Set<String> jarNameSet = new HashSet<>();
    protected Set<String> suffixSet  = new HashSet<>();

    protected AbstractClassTemplate(Class<? extends Annotation> annotation,
                                    String packages,
                                    String jarName,
                                    String suffix) {
        this.annotationSet.add(annotation);
        this.packageSet.add(packages);
        this.jarNameSet.add(jarName);
        this.suffixSet.add(suffix);
    }

    protected AbstractClassTemplate(Set<Class<? extends Annotation>> annotationSet,
                                    Set<String> packageSet,
                                    Set<String> jarNameSet,
                                    Set<String> suffixSet) {
        this.annotationSet.addAll(annotationSet);
        this.packageSet.addAll(packageSet);
        this.jarNameSet.addAll(jarNameSet);
        this.suffixSet.addAll(suffixSet);
    }

    protected AbstractClassTemplate(Set<Class<? extends Annotation>> annotationSet) {
        this(annotationSet, null, null, null);
    }

    protected AbstractClassTemplate(Set<Class<? extends Annotation>> annotationSet, Set<String> packageSet) {
        this(annotationSet, packageSet, null, null);
    }


    protected  void reload() {

    }

    /**
     * TODO  替换重复的包路径
     */
    /*
    private void replaceDuplicatePackageName() {
        if (ToolsKit.isEmpty(packageSet) ) {
           throw new EmptyNullException("包路径为空，请指定包路径");
        }

        Set<String> tmpPackageSet = new HashSet<>(packageSet.size());

        String[] packageArray = packageSet.toArray(new String[packageSet.size()]);

        Arrays.sort(packageArray, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (o1.length() > o2.length()) ? 0 : 1;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });

        for (Iterator<String> it = packageSet.iterator(); it.hasNext();) {
            String key = it.next();
            for(String packageName : packageArray) {
                if (key.startsWith(packageName)) {

                }
            }
        }
    }
    */

    /**
     * 取出所有指定包名及指定jar文件前缀的Class类
     * @return
     * @throws Exception
     */
    @Override
    public List<Class<?>> getList() throws Exception{
        List<Class<?>> classList = new ArrayList<>();
//        String classesRootPath = PathKit.duang().resource("").web();
//        String libRootPath = PathKit.duang().resource("").lib();
        for(String packageName : packageSet) {
            Enumeration<URL> urls = PathKit.duang().resource(packageName).paths();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (ToolsKit.isEmpty(url)) {
                    continue;
                }
                String protocol = url.getProtocol();
//                logger.debug(protocol +"                  "+url.getPath());
                if ("file".equalsIgnoreCase(protocol)) {
                    String packagePath = url.getPath().replaceAll("%20", " ");
                    addClass(classList, packagePath, packageName);
                } else if ("jar".equalsIgnoreCase(protocol)) {
                    // 若在 jar 包中，则解析 jar 包中的 entry
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = jarURLConnection.getJarFile();
                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                    while (jarEntries.hasMoreElements()) {
                        JarEntry jarEntry = jarEntries.nextElement();
                        String fileName = jarEntry.getName();
                        if (!jarNameSet.contains(fileName)) {
                            continue;
                        }
                        // 包含有.且不是.或/结尾的文件名
                        if(fileName.contains(".") && !fileName.endsWith("/") && !fileName.endsWith(".") && !fileName.endsWith(File.separator)) {
                            String suffix = fileName.substring(fileName.lastIndexOf("."));
                            String subFileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length() - suffix.length());
                            String filePkg = fileName.contains("/")? fileName.substring(0, fileName.length() - subFileName.length() - suffix.length() - 1).replaceAll("/", ".") : "";
                            // 执行添加类操作
                            doAddClass(classList, filePkg, fileName, suffix);
                        }
                    }
                }
                if(!Const.IS_RELOAD_SCANNING) {
                    //将所有URL添加到集合
                    Const.URL_LIST.add(url);
                }
            }
        }
        return classList;
    }

    /**
     * 添加类到List集合
     * @param classList
     * @param packagePath
     * @param packageName
     */
    private void addClass(List<Class<?>> classList, String packagePath, String packageName) {
        try {
            // 获取包名路径下的 文件或目录
            File[] files = new File(packagePath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() || pathname.getName().contains(".");
                }
            });
            if (ToolsKit.isNotEmpty(files)) {
                // 遍历文件或目录
                for (File file : files) {
                    String fileName = file.getName();
                    // 判断是否为文件或目录
                    if (file.isFile()) {
                        // 获取类名
                        String className = fileName.substring(0, fileName.lastIndexOf("."));
                        // 执行添加类操作
                        doAddClass(classList, packageName, className, fileName.substring(fileName.lastIndexOf("."), fileName.length()));
                    } else {
                        // 获取子包
                        String subPackagePath = fileName;
                        if (!ToolsKit.isEmpty(packagePath)) {
                            subPackagePath = packagePath + "/" + subPackagePath;
                        }
                        // 子包名
                        String subPackageName = fileName;
                        if (!ToolsKit.isEmpty(packageName)) {
                            subPackageName = packageName + "." + subPackageName;
                        }
                        // 递归调用
                        addClass(classList, subPackagePath, subPackageName);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("添加类出错！", e);
        }
    }

    private void doAddClass(List<Class<?>> filelist, String packageName, String fileName, String suffix) {
        if (!".class".equalsIgnoreCase(suffix)) {
            return;
        }
        doLoadClass(filelist, packageName, fileName, suffix);
    }


    /**
     * 子类实现具体方法
     * @param filelist
     * @param packageName
     * @param fileName
     * @param suffix
     */
    public abstract void doLoadClass(List<Class<?>> filelist, String packageName, String fileName, String suffix);

}
