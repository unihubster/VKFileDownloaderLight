package net.demo.vkfiledownloader;

import net.demo.vkfiledownloader.entities.DownloadParameters;
import net.demo.vkfiledownloader.utils.VKDocDownloadRunner;

public class Main {

    public static void main(String[] args) {

        VKDocDownloadRunner vkDocDownloadRunner = new VKDocDownloadRunner();
        DownloadParameters downloadParameters = vkDocDownloadRunner.consoleInputRunner();
        vkDocDownloadRunner.downloadRunner(downloadParameters);

    }
}
