package net.demo.vkfiledownloader.entities;

public class DownloadParameters {
    private String page;
    private int pageType; //see below 0 - unknown, 1 - topic, 2 - wall, 3 - hashtag search
    private String saveDir;
    private int maxsize;

    public DownloadParameters(String page, int pageType, String saveDir, int maxsize) {
        this.page = page;
        this.pageType = pageType;
        this.saveDir = saveDir;
        this.maxsize = maxsize;
    }

    public String getPage() {
        return page;
    }

    public int getPageType() {
        return pageType;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public int getMaxsize() {
        return maxsize;
    }
}
