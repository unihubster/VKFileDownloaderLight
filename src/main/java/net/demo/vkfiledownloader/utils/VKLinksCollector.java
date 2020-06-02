package net.demo.vkfiledownloader.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

//TODO validation doc is it available?
//TODO search pdf not only in href text on the wall
public class VKLinksCollector {

    public Map<String, String> collect(String page, int pageType, String saveDir) {//TODO change Map to List of download ojects (name, number page with link (link to page), link, size
        Map<String, String> links = new HashMap<>();

        switch (pageType) {
            case 1:
                findAllDocLinksInTopic(links, page, saveDir);
                break;
            case 2:
                findAllDocLinksOnWall(links, page, saveDir);
                break;
            case 3:
                findAllDocLinksInHashtagSearch(links, page, saveDir);
                break;
            default:
                System.out.println("[utils.VKLinksCollector/INFO]: Unknown page type: " + pageType);
        }

        return links;
    }


    private Map<String, String> findAllDocLinksInTopic(Map<String, String> links, String page, String saveDir) { //TODO saveDir can be removed after adding doc download?!

        int pageLastNum = 0;
        int pageCurrentNum = 0;

        do {
            try {
                Document doc = Jsoup.connect(page).get();

//            List<String> pages = (doc.select("div.result-wrap a[href^=/video/view/]")).eachAttr("abs:href");
                if (pageCurrentNum == 0) {
                    List<String> pages = doc.getElementsByClass("pg_pages bt_pages").first()
                            .getElementsByClass("pg_lnk fl_l").select("a[href]").eachAttr("abs:href"); //TODO is it possible to simplify it?

                    if (!pages.isEmpty()) {
                        page = doc.getElementsByClass("pg_pages bt_pages").first()
                                .getElementsByClass("pg_lnk_sel fl_l").select("a[href]").attr("abs:href");//("abs:href"); //TODO is it possible to simplify it?
                        String last_href = pages.get(pages.size() - 1);
                        pageLastNum = Integer.parseInt(last_href.substring(last_href.lastIndexOf("offset=") + 7));
                        pageLastNum = checkLastPageNum(pageLastNum);
                    } else {
                        page += "?offset=" + pageCurrentNum;
                    }
                }

                links = findAllDocLinks(links, doc, saveDir);

            } catch (IOException e) {
                e.printStackTrace();
            }

            page = page.replace("offset=" + pageCurrentNum, "offset=" + (pageCurrentNum += 20));

        } while (pageCurrentNum < pageLastNum);

        return links;
    }


    private Map<String, String> findAllDocLinksOnWall(Map<String, String> links, String page, String saveDir) { //TODO saveDir can be removed after adding doc download?!

        try {
            Document doc = Jsoup.connect(page).get();
            page = doc.getElementsByClass("ui_tab ui_tab_sel").first()
                    .select("a[href]").attr("abs:href");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return findAllDocLinksInHashtagSearch(links, page, saveDir);
    }


    private Map<String, String> findAllDocLinksInHashtagSearch(Map<String, String> links, String page, String saveDir) { //TODO saveDir can be removed after adding doc download?!

        int pageLastNum = 0;
        int pageCurrentNum = 0;

        do {
            try {
                System.out.println("[SYSTEM/INFO]: Search docs on " + page);
                Document doc = Jsoup.connect(page).get();

//            List<String> pages = (doc.select("div.result-wrap a[href^=/video/view/]")).eachAttr("abs:href");
                if (pageCurrentNum == 0) {
                    List<String> pages = doc.getElementsByClass("pg_pages").first()
                            .getElementsByClass("pg_lnk fl_l").select("a[href]").eachAttr("abs:href"); //TODO is it possible to simplify it?

                    if (!pages.isEmpty()) {
                        page = doc.getElementsByClass("pg_pages").first()
                                .getElementsByClass("pg_lnk_sel fl_l").select("a[href]").attr("abs:href");//("abs:href"); //TODO is it possible to simplify it?
                        String last_href = pages.get(pages.size() - 1);
                        pageLastNum = Integer.parseInt(last_href.substring(last_href.lastIndexOf("offset=") + 7));
                        pageLastNum = checkLastPageNum(pageLastNum);
                    } else {
                        page += "?offset=" + pageCurrentNum;
                    }
                }

                links = findAllDocLinks(links, doc, saveDir);

            } catch (IOException e) {
                System.out.println("    Can't connect to " + page);
                e.printStackTrace();
            }

            page = page.replace("offset=" + pageCurrentNum, "offset=" + (pageCurrentNum += 20));

        } while (pageCurrentNum < pageLastNum);

        return links;
    }


    private Map<String, String> findAllDocLinks(Map<String, String> links, Document doc, String saveDir) { //TODO saveDir can be removed after adding doc download?!
        Elements hrefs = doc.getElementsByClass("page_doc_title");
        for (Element lin : hrefs //TODO download microsoft docs
        ) {
            String href = lin.attr("abs:href");
            if (
                    lin.text().toLowerCase().endsWith("doc") ||
                            lin.text().toLowerCase().endsWith("docx")
            ) {
                try {
                    HttpDownloadUtility.passedUrlList(href, -1, saveDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (lin.text().toLowerCase().endsWith("pdf")) {
                try {
                    Document docPDF = Jsoup.connect(href).get();
                    String hrefPDF = docPDF.getElementsByClass("iframe").attr("src");
                    links.put(lin.text(), hrefPDF);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                links.put(lin.text(), href);
            }
        }
        return links;
    }


    private int checkLastPageNum(int pageLastNum) {
        int pagesLastDefaultMax = 5; //Default max number of pages to look through before ask user
        int pageLast = pageLastNum / 20 + 1;
        int pagesMax = -1;

        if (pageLast > pagesLastDefaultMax) {
            System.out.println("    There are " + pageLast + " pages" +
                    "\n    How many pages look through do you need?\n" +
                    "   Please, type 0 if you need all pages or put your value less than " + pageLast + ":");
            while (pagesMax < 0) {

                try {
                    pagesMax = new Scanner(System.in).nextInt();
                } catch (InputMismatchException ex) {
                    System.out.println("    Your input isn't a number\n" +
                            "   Please, type 0 if you need all pages or put your value less than " + pageLast + ":");
                }

                if (pagesMax < 0) {
                    System.out.println("    Your input is wrong\n" +
                            "   Please, type 0 if you need all pages or put your value less than " + pageLast + ":");
                    continue;
                } else if (pagesMax == 0) {
                    pagesMax = pageLast;
                } else if (pagesMax > pageLastNum) {
                    pagesMax = -1;
                    System.out.println("    Your number is higher than total pages number (" + pageLast + ")\n" +
                            "   Please, type 0 if you need all pages or put your value less than " + pageLast + ":");
                }

                pageLastNum = pagesMax * 20;
            }
        }
        return pageLastNum;
    }


}
