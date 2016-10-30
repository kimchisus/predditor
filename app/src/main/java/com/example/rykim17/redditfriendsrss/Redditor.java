package com.example.rykim17.redditfriendsrss;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by rykim17 on 2016-10-30.
 */

//    DON'T REMOVE THIS BECAUSE IT IS USED TO CONVERT TO PARCELABLE
//    public class Redditor {
//        private String userName;
//        ArrayList<Comment> comments;
//
//        public Redditor(String userName, ArrayList<String> titles, ArrayList<String> contents, ArrayList<String> urls, ArrayList<String> times, ArrayList<String> subReddits) {
//            comments = new ArrayList<Comment>();
//            this.userName = userName;
//
//            for(int i = 0; i < titles.size(); i++) {
//                this.comments.add(new Comment(titles.get(i), contents.get(i), urls.get(i), times.get(i), subReddits.get(i)));
//            }
//        }
//
//        public String getUserName() { return this.userName; }
//        public ArrayList<Comment> getComments() { return this.comments; }
//    }

public class Redditor implements Parcelable {
    private String userName;
    ArrayList<Comment> comments;

    public Redditor(String userName, ArrayList<String> titles, ArrayList<String> contents, ArrayList<String> urls, ArrayList<String> times, ArrayList<String> subReddits) {
        comments = new ArrayList<Comment>();
        this.userName = userName;

        for(int i = 0; i < titles.size(); i++) {
            this.comments.add(new Comment(titles.get(i), contents.get(i), urls.get(i), times.get(i), subReddits.get(i)));
        }
    }

    public String getUserName() { return this.userName; }
    public ArrayList<Comment> getComments() { return this.comments; }

    protected Redditor(Parcel in) {
        userName = in.readString();
        if (in.readByte() == 0x01) {
            comments = new ArrayList<Comment>();
            in.readList(comments, Comment.class.getClassLoader());
        } else {
            comments = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        if (comments == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(comments);
        }
    }

    @SuppressWarnings("unused")
    public final Parcelable.Creator<Redditor> CREATOR = new Parcelable.Creator<Redditor>() {
        @Override
        public Redditor createFromParcel(Parcel in) {
            return new Redditor(in);
        }

        @Override
        public Redditor[] newArray(int size) {
            return new Redditor[size];
        }
    };
}
