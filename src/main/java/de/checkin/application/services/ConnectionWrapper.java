package de.checkin.application.services;


import java.io.IOException;
import org.jsoup.Jsoup;

public class ConnectionWrapper {

  public String getHtmlResponse(String url) throws IOException {
    return Jsoup.connect(url).get().body().html();
  }
}