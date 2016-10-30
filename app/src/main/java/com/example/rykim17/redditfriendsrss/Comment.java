package com.example.rykim17.redditfriendsrss;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rykim17 on 2016-10-30.
 */


public class Comment implements Parcelable {
    private String title;
    private String content;
    private String url;
    private String time;
    private String subReddit;

    public Comment(String title, String content, String url, String time, String subReddit) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.time = time;
        this.subReddit = subReddit;
    }

    public String getTitle() {
        return this.title;
    }
    public String getContent() { return this.content; }
    public String getUrl() {
        return this.url;
    }
    public String getTime() { return this.time; }
    public String getSubReddit() { return this.subReddit; }

    protected Comment(Parcel in) {
        title = in.readString();
        content = in.readString();
        url = in.readString();
        time = in.readString();
        subReddit = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(time);
        dest.writeString(subReddit);
    }

    @SuppressWarnings("unused")
    public final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}

//    public class Comment {
//        private String title;
//        private String content;
//        private String url;
//        private String time;
//        private String subReddit;
//
//        public Comment(String title, String content, String url, String time, String subReddit) {
//            this.title = title;
//            this.content = content;
//            this.url = url;
//            this.time = time;
//            this.subReddit = subReddit;
//        }
//
//        public String getTitle() {
//            return this.title;
//        }
//        public String getContent() { return this.content; }
//        public String getUrl() {
//            return this.url;
//        }
//        public String getTime() { return this.time; }
//        public String getSubReddit() { return this.subReddit; }
//    }