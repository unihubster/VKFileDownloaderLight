package net.demo.vkfiledownloader.utils;

import net.demo.vkfiledownloader.entities.DownloadParameters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class VKDocDownloadRunner {

    public DownloadParameters consoleInputRunner() {//TODO ask user about download files or only to search link
        int pageType = 0; //see below 1 - if it's topic, 2 - if it's wall, 3 - if it's hashtag search

        String page = "";
        boolean tryAgain = true;
        while (tryAgain) {
            System.out.println("Please, put URL to first VK page:"); //TODO what if link isn't for first page (with "?offset=")?
            page = new Scanner(System.in).next().trim();
            if (!page.startsWith("https://")) {
                System.out.println("    Wrong URL! Link has to start from \"https://\"\n" +
                        "   Please, try again:");
                //TODO add choice for exit
            } else {
                if (!page.startsWith("https://vk.com")) {
                    System.out.println("    Wrong URL! Link has to contain \"https://vk.com\"");
                } else {
                    System.out.println("Is link to topic, to VK community wall or hashtag search?\n" +
                            "1 - topic\n" +
                            "2 - wall\n" +
                            "3 - hashtag search");

                    try {
                        pageType = new Scanner(System.in).nextInt();
                        switch (pageType) {
                            case 1:
                                if (!page.contains("https://vk.com/topic-")) {
                                    System.out.println("    This is not link to discussion topic");
                                }
                                tryAgain = false;
                                break;

                            case 2: //TODO check link
                                tryAgain = false;
                                break;

                            case 3: //TODO check link
                                tryAgain = false;
                                break;

                            default:
                                System.out.println("    Your input isn't 1, 2 or 3\n" +
                                        "   Please, insert your link again:");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("    Your input is wrong\n" +
                                "   Please, try again");
                    }

                }
            }
        }

        String saveDir = userAskingSaveDir();
        boolean downloadYes = userAskingDownload();
        int maxsize;
        if (downloadYes) {
            maxsize = userAskingMaxSize();
        } else {
            maxsize = -1;
        }

        System.out.println("    Thank you, input values are correct\n" +
                "[SYSTEM/INFO]: The downloader is running...");

        return new DownloadParameters(page, pageType, saveDir, maxsize);
    }

    public void downloadRunner(DownloadParameters downloadParameters) {
        String page = downloadParameters.getPage();
        int pageType = downloadParameters.getPageType();
        String saveDir = downloadParameters.getSaveDir();
        int maxsize = downloadParameters.getMaxsize();
        Map<String, String> links = new VKLinksCollector().collect(page, pageType, saveDir); //TODO change to 'List <FilesToDownload>' with several fields

        links.forEach((name, link) -> { //TODO save to file one by one links or parts by parts
            try (Writer out = new OutputStreamWriter(
                    new FileOutputStream(saveDir + File.separator + "__links.csv", true), StandardCharsets.UTF_8)) {
                out.write((name + "\t" + link));
                out.write("\n");
            } catch (IOException e) {
                System.out.println("[SYSTEM/INFO]: Can't save __links.csv");
                e.printStackTrace();
            }
        });

        if (maxsize >= 0) {
            links.forEach((name, link) -> {
                System.out.println("[SYSTEM/INFO]: Trying to download... " + name);
                HttpDownloadUtility.downloadFile(link, saveDir, maxsize);
            });
            System.out.println("[SYSTEM/INFO]: All found files have been downloaded");
        } else {
            System.out.println("[SYSTEM/INFO]: Links have been collected");
        }
    }


    private String userAskingSaveDir() {
        System.out.println("Please, put path to output folder:");
        String saveDir;
        while (true) {
            saveDir = new Scanner(System.in).next().trim();
//        saveDir = "C:\\Users\\Astrovar\\Documents\\IdeaProjects\\_My\\test";
            File dir = new File(saveDir);
            if (!dir.isDirectory()) {
                System.out.println("    Such path is not exist. Try to create it?\n" +
                        "1 - yes\n" +
                        "2 - no");
                boolean create = false;
                if (new Scanner(System.in).next().trim().contentEquals("1")) {
                    create = true;
                }
                if (create) {
                    if (!dir.mkdir()) {
                        System.out.println("    Can't to create folder in the path\n" +
                                "   Please, try another path:");
                    } else break;
                } else {
                    System.out.println("    Please, try another path:");
                }
            } else break;
        }
        return saveDir;
    }

    private boolean userAskingDownload() {
        boolean downloadYes = false;
        int downloadYesInt = -1;
        boolean tryAgain = true;
        while (tryAgain) {
            System.out.println("Do you want to download file or just to get links?\n" +
                    "0 - to get links\n" +
                    "1 - to download");
            try {
                downloadYesInt = new Scanner(System.in).nextInt();
            } catch (InputMismatchException ignored) {
            }

            switch (downloadYesInt) {
                case 0:
                    downloadYes = false;
                    tryAgain = false;
                    break;
                case 1:
                    downloadYes = true;
                    tryAgain = false;
                    break;
                default:
                    System.out.println("    Your input isn't 0 or 1\n" +
                            "   Please, try again");
            }
        }
        return downloadYes;
    }

    private int userAskingMaxSize() {
        System.out.println("Please, insert max file size in MBytes (INTEGER) to download (0 - without any limit):");
        int maxsize = -1;
        while (maxsize < 0) {//TODO can it be simplified?
            try {
                maxsize = new Scanner(System.in).nextInt();
            } catch (InputMismatchException ignored) {
            }
            if (maxsize < 0) {
                System.out.println("    Value have to be integer, equal 0 or higher\n" +
                        "   Please, try again");
            }
        }
        return maxsize;
    }

}