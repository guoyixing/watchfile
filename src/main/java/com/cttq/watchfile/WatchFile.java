package com.cttq.watchfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @date 2018-09-28 13:45
 * @since 1.0.0
 */
public class WatchFile {
    private static final Logger logger = LoggerFactory.getLogger(WatchFile.class);
    private static ExecutorService fixedThreadPool = Executors.newCachedThreadPool();
    private WatchService ws;
    private String listenerPath;

    private WatchFile(String path) {
        try {
            ws = FileSystems.getDefault().newWatchService();
            this.listenerPath = path;
            start();
        } catch (IOException e) {
            logger.error("监听文件失败：{}", e);
        }
    }

    private void start() {
        fixedThreadPool.execute(new Listner(ws, this.listenerPath));
    }

    public static void addListener(String path) throws IOException {
        String[] split = path.split(",");
        for (String str : split) {
            WatchFile resourceListener = new WatchFile(str);
            Paths.get(str).register(resourceListener.ws, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_CREATE);
            File file = new File(str);
            LinkedList<File> fList = new LinkedList<>();
            fList.addLast(file);
            while (fList.size() > 0) {
                File f = fList.removeFirst();
                if (f.listFiles() == null) {
                    continue;
                }
                for (File file2 : f.listFiles()) {
                    //下一级目录
                    if (file2.isDirectory()) {
                        fList.addLast(file2);
                        //依次注册子目录
                        Paths.get(file2.getAbsolutePath()).register(resourceListener.ws
                                , StandardWatchEventKinds.ENTRY_CREATE
                                , StandardWatchEventKinds.ENTRY_MODIFY
                                , StandardWatchEventKinds.ENTRY_DELETE);
                    }
                }
            }
        }
    }
}
