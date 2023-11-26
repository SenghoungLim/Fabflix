package edu.uci.ics.fabflixmobile.data;

public class Constants {
    private final String host = "13.57.239.215";
    private final String port = "8443";
    private final String domain = "project1";
//    private final String host = "10.0.2.2";
//    private final String port = "8080";
//    private final String domain = "project1";
    private final String url = "https://" + host + ":" + port + "/" + domain;
    public String getUrl() {
        return url;
    }
}
