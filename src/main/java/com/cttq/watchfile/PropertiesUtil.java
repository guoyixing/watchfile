package com.cttq.watchfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    public static String getValue(String key) {
        Properties prop = new Properties();
        try {
//            InputStream in = new FileInputStream(new File("D://watch.properties"));
            InputStream in = new FileInputStream(new File("watch.properties"));
            //装载配置文件
            prop.load(in);
        } catch (FileNotFoundException e) {
            logger.error("找不到配置文件：{}", e);
        } catch (IOException e) {
            logger.error("发生IO异常：{}", e);
        }
        //返回获取的值
        return prop.getProperty(key);
    }
}