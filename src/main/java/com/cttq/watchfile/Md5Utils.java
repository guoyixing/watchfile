package com.cttq.watchfile;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class Md5Utils {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * 获取文件的md5
     *
     * @param path 文件路径
     * @author 郭一行
     * @date 2018/6/13 16:05
     * @since 1.0.0
     */
    public static String getFileMd5(String path) throws IOException {
        //获取失败后的等待时间
        String sleepTime = PropertiesUtil.getValue("sleepTime");
        FileInputStream fis = null;
        boolean flag = true;
        while (flag) {
            try {
                //读取文件
                fis = new FileInputStream(path);
                //文件读取成功停止循环
                flag = false;
                //获取文件的md5
                return DigestUtils.md5Hex(fis);
            } catch (Exception e) {
                logger.error("文件读取失败：{}", e);
                try {
                    Thread.sleep(Integer.valueOf(sleepTime));
                } catch (InterruptedException e1) {
                    logger.error("读取文件失败后无法休眠：{}", e);
                }
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        logger.error("获取md5后流无法正常关闭：{}", e);
                    }
                }
            }
        }
        return "";
    }
}