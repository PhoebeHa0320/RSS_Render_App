package com.projectteam.newsapp.RSSFeed;

import java.util.List;

public class RSSObject {
    public String status;
    public Feed feed;
    public List<Item> items;

    public RSSObject(String status, Feed feed, List<Item> items) {
        this.status = status;
        this.feed = feed;
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

}
