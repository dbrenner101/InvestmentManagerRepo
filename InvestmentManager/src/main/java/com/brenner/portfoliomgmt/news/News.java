package com.brenner.portfoliomgmt.news;

import java.net.URL;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * POJO container for news articles.
 * 
 * @author dbrenner
 *
 */
public class News {

    @JsonAlias(value="datetime")
    private Date publishedDate;
    private String headline;
    private String source;
    @JsonAlias(value="url")
    private URL articleUrl;
    private String summary;
    private String related;
    @JsonAlias(value="image")
    private URL imageUrl;
    
    
    public Date getPublishedDate() {
        return publishedDate;
    }
    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }
    public String getHeadline() {
        return headline;
    }
    public void setHeadline(String headline) {
        this.headline = headline;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public URL getArticleUrl() {
        return articleUrl;
    }
    public void setArticleUrl(URL articleUrl) {
        this.articleUrl = articleUrl;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public String getRelated() {
        return related;
    }
    public void setRelated(String related) {
        this.related = related;
    }
    public URL getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
        builder.append("publishedDate", this.publishedDate)
            .append("headline", this.headline)
            .append("source", this.source)
            .append("articleUrl", this.articleUrl)
            .append("summary", this.summary)
            .append("related", this.related)
            .append("imageUrl", this.imageUrl);
        return builder.toString();
    }
    
}
