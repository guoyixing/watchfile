package com.cttq.watchfile;

import java.io.IOException;

/**
 * @date 2018-09-28 14:19
 * @since 1.0.0
 */
public class Main {
    public static void main(String[] args) throws IOException {
        WatchFile.addListener(PropertiesUtil.getValue("dirPath"));
    }
}
