package com.example.rykim17.redditfriendsrss;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rykim17 on 2016-10-30.
 */


public class Comment implements Parcelable, Comparable<Comment> {
    private String title;
    private String content;
    private String url;
    private String time;
    private String subReddit;
    private String id;

    public Comment(String title, String content, String url, String time, String subReddit, String id) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.time = time;
        this.subReddit = subReddit;
        this.id = id;
    }

    @Override
    public int compareTo(Comment o) {
        return o.id.equals(this.id) ? 1 : 0;
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
    public String getIds() { return this.id; }

    protected Comment(Parcel in) {
        title = in.readString();
        content = in.readString();
        url = in.readString();
        time = in.readString();
        subReddit = in.readString();
        id = in.readString();
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
        dest.writeString(id);
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
//        private String id;
//
//        public Comment(String title, String content, String url, String time, String subReddit, String id) {
//            this.title = title;
//            this.content = content;
//            this.url = url;
//            this.time = time;
//            this.subReddit = subReddit;
//            this.id = id;
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
//        public String getId() { return this.id; }
//    }