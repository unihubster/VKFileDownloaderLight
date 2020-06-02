package net.demo.vkfiledownloader.utils;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//modified code from http://www.codejava.net/java-se/networking/use-httpurlconnection-to-download-file-from-an-http-url

/**
 * Downloads a file from a URL
 */
public class HttpDownloadUtility {

    private static final int BUFFER_SIZE = 4096;

    private HttpDownloadUtility() {
    }

    /**
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @param maxsize upper limit of file size to download
     */
    public static void downloadFile(String fileURL, String saveDir, int maxsize) {
        maxsize *= 1024 * 1024;
        try {
            URL url = new URL(fileURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
//                String contentType = httpConn.getContentType();
                String location = httpConn.getHeaderField("location");
                int contentLength = httpConn.getContentLength();
                String fileURLCurrent = httpConn.getURL().toExternalForm();

                if (contentLength <= maxsize || maxsize == 0) {
                    //0 - without limits
                    // "maxsize = 0" means to download all files

                    // extracts file name from header field
                    if (disposition != null && (disposition.indexOf("filename=")) > 0) {
                        int index = disposition.indexOf("filename=") - 1;
                        if (index > 0) {
                            fileName = disposition.substring(index + 10,
                                    disposition.length() - 1);
                        }
                    } else {
                        // extracts file name from URL
                        if (fileURLCurrent.contains("?")) {
                            fileURLCurrent = fileURLCurrent.substring(0, fileURLCurrent.lastIndexOf('?'));
                            System.out.println(fileURLCurrent);
                        }
                        fileName = fileURLCurrent.substring(fileURLCurrent.lastIndexOf('/') + 1);
                    }
/*
                    System.out.println("Content-Type = " + contentType);
                    System.out.println("Content-Disposition = " + disposition);
*/
                    System.out.println("Content-Length = " + contentLength);
                    System.out.println("fileName = " + fileName);
                    System.out.println("location = " + location);

                    // opens input stream from the HTTP connection
                    InputStream inputStream = httpConn.getInputStream();
                    String saveFilePath = saveDir + File.separator + fileName;
                    System.out.println("[SYSTEM/INFO]: Downloading to...  " + saveFilePath);

                    // opens an output stream to save into file
                    try (FileOutputStream outputStream = new FileOutputStream(saveFilePath)) {
                        int bytesRead = -1;
                        byte[] buffer = new byte[BUFFER_SIZE];
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    inputStream.close();

                    System.out.println("[SYSTEM/INFO]: Downloading has finished...");
                    downloadedList(fileName, contentLength, saveDir);
                } else {
                    System.out.println(fileURL + " has size upper your limit, it's "
                            + new BigDecimal((double) contentLength / 1024 / 1024)
                            .setScale(2, BigDecimal.ROUND_HALF_DOWN) + " MBytes");
                    passedUrlList(fileURL, contentLength, saveDir);
                }
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadedList(String fileName, int contentLength, String saveDir) throws IOException { //TODO make input values more generalized (object with parameters to save)
        try (Writer out = new OutputStreamWriter(
                new FileOutputStream(saveDir + File.separator + "__downloaded.txt", true), StandardCharsets.UTF_8)) {//TODO write data to .csv
            out.write((fileName + "  "));
//        out.write((Integer.valueOf(contentLength).toString() + " bytes, "));
            out.write((BigDecimal.valueOf((double) contentLength / 1024 / 1024)
                    .setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + " MBytes"));
            out.write("\n");
        }
    }

    static void passedUrlList(String fileURL, int contentLength, String saveDir) throws IOException { //TODO make input values more generalized (object with parameters to save)
        try (Writer out = new OutputStreamWriter(
                new FileOutputStream(saveDir + File.separator + "__passed.txt", true), StandardCharsets.UTF_8)) {//TODO write data to .csv
            out.write((fileURL + "  "));
//        out.write((Integer.valueOf(contentLength).toString() + " bytes, "));
            out.write((BigDecimal.valueOf((double) contentLength / 1024 / 1024)
                    .setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + " MBytes"));
            out.write("\n");
        }
    }


/*
    public static void downloadedList(String fileName, int contentLength, String saveDir) throws IOException { //TODO make input values more generalized (object with parameters to save)
        try (FileOutputStream out = new FileOutputStream(saveDir + File.separator + "__downloaded.txt", true)) {//TODO write data to .csv
            out.write((fileName + "  ").getBytes());
//        out.write((Integer.valueOf(contentLength).toString() + " bytes, ").getBytes());
            out.write((BigDecimal.valueOf((double) contentLength / 1024 / 1024)
                    .setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + " MBytes").getBytes());
            out.write("\n".getBytes());
        }
    }

    public static void passedUrlList(String fileURL, int contentLength, String saveDir) throws IOException { //TODO make input values more generalized (object with parameters to save)
        try (FileOutputStream out = new FileOutputStream(saveDir + File.separator + "__passed.txt", true)) {//TODO write data to .csv
            out.write((fileURL + "  ").getBytes());
//        out.write((Integer.valueOf(contentLength).toString() + " bytes, ").getBytes());
            out.write((BigDecimal.valueOf((double) contentLength / 1024 / 1024)
                    .setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + " MBytes").getBytes());
            out.write("\n".getBytes());
        }
    }
*/
}