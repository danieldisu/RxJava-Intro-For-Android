package com.danieldisu.rxjavaintro;

import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public class RestClient {

    protected interface HNService {

        @GET("/item/{itemId}.json")
        public Observable<HNItem> getItem(@Path("itemId") String itemId);

        @GET("/newstories.json")
        public Observable<NewStories> getNewStories();
    }

    private static HNService hnService;

    public static HNService getHNService() {
        if (RestClient.hnService == null) {
            RestAdapter retrofit = new RestAdapter.Builder()
                    .setEndpoint("https://hacker-news.firebaseio.com/v0")
                    .build();

            RestClient.hnService = retrofit.create(HNService.class);
        }

        return RestClient.hnService;
    }

    public static class HNItem {
        String by;
        int descendants;
        String id;
        int[] kids;
        int score;
        long time;
        String title;
        String type;
        String url;
    }

    protected static class NewStories extends ArrayList<String>{
    }
}
