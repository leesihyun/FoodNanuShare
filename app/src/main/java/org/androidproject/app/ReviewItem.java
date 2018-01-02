package org.androidproject.app;

/**
 * Created by yeonjae on 2017-06-10.
 */

public class ReviewItem {

    private String reviewTitle ;
    private String reviewContent ;
    private String user_key;

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }


    public String getReviewTitle() {
        return reviewTitle;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }
}
