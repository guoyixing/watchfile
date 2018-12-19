package com.cttq.watchfile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2018-09-28 14:25
 * @since 1.0.0
 */
public class EntryCreate {
    public static String getParam(String path) throws IOException {
        //获取监听路径
        String dirPath = PropertiesUtil.getValue("dirPath");
        //获取监听路径对应的url预设值
        String[] downloadUrlParam = PropertiesUtil.getValue("downloadUrl").split(",");
        //配对上的url预设值
        String downloadUrl = "";
        int dirPathskey = 0;
        String[] dirPaths = dirPath.split(",");
        for (int i = 0; i < dirPaths.length; i++) {
            //这里必须使用File让路径地址保持格式统一，配置文件中的dirPath和downloadUrl保持一对一的且所以位置一样
            if (new File(path).getPath().contains(new File(dirPaths[i]).getPath())
                    && dirPathskey < dirPaths[i].length()) {
                downloadUrl = downloadUrlParam[i];
                dirPathskey = dirPaths[i].length();
            }
        }

        File file = new File(path);
        StringBuilder sb = new StringBuilder();
        if (file.exists() && file.isFile()) {
            String ftpUser = PropertiesUtil.getValue("ftpUser");
            if (ftpUser == null || "".equals(ftpUser)) {
                ftpUser = file.getParentFile().getName();
            }
            String ftp = file.getParentFile().getName();
            String downloadAddr = PropertiesUtil.getValue("downloadAddr");
            String fileMd5 = Md5Utils.getFileMd5(path);
            if ("".equals(fileMd5)) {
                throw new IOException("无法获取Md5");
            }
            //获取文件大小
            sb.append("fileSize=")
                    .append(file.length())
                    .append("&")
                    //获取文件md5
                    .append("fileMd5=")
                    .append(fileMd5)
                    .append("&")
                    //获取用户名
                    .append("ftpUser=")
                    .append(ftpUser)
                    .append("&")
                    //下载地址
                    .append("downloadAddr=")
                    .append(downloadAddr)
                    .append("/")
                    .append(downloadUrl)
                    .append("/")
                    .append(ftp)
                    .append("/")
                    .append(file.getName());
        }
        return sb.toString();
    }
}
