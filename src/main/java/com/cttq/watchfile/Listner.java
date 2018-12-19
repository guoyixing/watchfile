package com.cttq.watchfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Listner implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Listner.class);
    private WatchService service;
    private String rootPath;
    private String fileName;

    public Listner(WatchService service, String rootPath) {
        this.service = service;
        //文件目录
        this.rootPath = rootPath;
    }

    @Override
    public void run() {
        String standSuffix = PropertiesUtil.getValue("standSuffix");
        List<String> standSuffixList = new ArrayList<>();
        if (standSuffix != null && !"".equals(standSuffix)) {
            standSuffixList = Arrays.asList(standSuffix.split(","));
        }
        try {
            while (true) {
                boolean flag = true;
                int count = 0;
                while (flag && count <= 3) {
                    count++;
                    try {
                        WatchKey watchKey = service.take();
                        List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                        for (WatchEvent<?> event : watchEvents) {
                            //新增文件
                            if ("ENTRY_CREATE".equals(event.kind().toString())) {
                                String finishWaitTime = PropertiesUtil.getValue("finishWaitTime");
                                //等待ftp完全上传成功
                                Thread.sleep(Integer.valueOf(finishWaitTime));
                                //获取文件名称
                                fileName = event.context().toString();
                                if (!standSuffixList.contains(fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase())) {
                                    logger.error("{},文件格式错误",fileName);
                                    continue;
                                }
                                //获取文件地址
                                Path path = (Path) watchKey.watchable();
                                logger.info("[{}{}]文件发生了[{}]事件", path, event.context(), event.kind());
                                String param = EntryCreate.getParam(path + File.separator + event.context());
                                HttpRequest.sendPost(param);
                                logger.info("{}文件处理结束，发送内容为{}", event.context(), param);
                            }
                        }
                        flag = false;
                        watchKey.reset();
                    } catch (InterruptedException e) {
                        logger.error("{}发生InterruptedException异常：{}", fileName, e);
                    } catch (ConnectException e) {
                        logger.error("{}发送http请求失败：{}", fileName, e);
                    } catch (IOException e) {
                        logger.error("{}发生IOException异常：{}", fileName, e);
                    }
                }
            }
        } finally {
            try {
                service.close();
            } catch (IOException e) {
                logger.error("发生IOException异常：{}", e);
            }
        }
    }

}