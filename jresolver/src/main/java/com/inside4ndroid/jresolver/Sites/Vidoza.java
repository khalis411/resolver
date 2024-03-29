package com.inside4ndroid.jresolver.Sites;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.inside4ndroid.jresolver.Jresolver;
import com.inside4ndroid.jresolver.Model.Jmodel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.inside4ndroid.jresolver.Utils.Utils.getDomainFromURL;

import android.util.Log;

public class Vidoza {
    public static void fetch(String url, final Jresolver.OnTaskCompleted onTaskCompleted){
        url = fixURL(url);
        if (url!=null) {
            AndroidNetworking.get(url)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            ArrayList<Jmodel> jModels = parse(response);
                            if (jModels!=null) {
                                onTaskCompleted.onTaskCompleted(jModels, false);
                            }else onTaskCompleted.onError();
                        }

                        @Override
                        public void onError(ANError anError) {
                            onTaskCompleted.onError();
                        }
                    });
        }else onTaskCompleted.onError();
    }

    private static String fixURL(String url){
        if (!url.contains("embed-")) {
            final String regex = "net/([^']*)";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                String id = matcher.group(1);
                if (id.contains("/")) {
                    id = id.substring(0, id.lastIndexOf("/"));
                }
                url = getDomainFromURL(url)+"/embed-" + id;
            } else {
                return null;
            }
        }
        return url;
    }

    private static ArrayList<Jmodel> parse(String response){

        final Pattern pattern = Pattern.compile("src.*?\"(.*?)\",", Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            Jmodel jModel = new Jmodel();
            jModel.setUrl(matcher.group(1));
            jModel.setQuality("Normal");
            ArrayList<Jmodel> jModels = new ArrayList<>();
            jModels.add(jModel);
            return jModels;
        }

        return null;
    }
}
