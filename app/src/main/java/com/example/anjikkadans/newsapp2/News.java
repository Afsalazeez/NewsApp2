package com.example.anjikkadans.newsapp2;

public class News {
    private String newsTitle;
    private String newsType;
    private String newsURL;
    private String newsTopic;
    private String newsAuthor;

    public News(String newsTitle, String newsType, String newsURL, String newsTopic, String author) {
        this.newsTitle = newsTitle;
        this.newsType = newsType;
        this.newsURL = newsURL;
        this.newsTopic = newsTopic;
        this.newsAuthor = author;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsType() {
        return newsType;
    }

    public String getNewsURL() {
        return newsURL;
    }

    public String getNewsTopic() {
        return newsTopic;
    }

    public String getNewsAuthor() {
        return newsAuthor;
    }

    @Override
    public String toString() {
        return "News{" +
                "newsTitle='" + newsTitle + '\'' +
                ", newsType='" + newsType + '\'' +
                ", newsURL='" + newsURL + '\'' +
                ", newsTopic='" + newsTopic + '\'' +
                ", newsAuthor='" + newsAuthor + '\'' +
                '}';
    }
}
