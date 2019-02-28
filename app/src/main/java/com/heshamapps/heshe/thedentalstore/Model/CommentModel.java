package com.heshamapps.heshe.thedentalstore.Model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;


@IgnoreExtraProperties
public class CommentModel implements Serializable {



   private String Comment , profilePic , CommentUser ;
    private String CommentDate;
    public CommentModel() {
    }


    public CommentModel(String Comment, String CommentDate, String profilePic, String CommentUser) {
        this.Comment = Comment;
        this.CommentDate = CommentDate;
        this.profilePic = profilePic;
        this.CommentUser = CommentUser;    }

    public String getComment() {
        return this.Comment;
    }

/*    public void setComment(String Comment) {
        this.comment = comment;
    }*/

    public String getCommentDate() {
        return this.CommentDate;
    }

  /*   public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }*/

    public String getProfilePic() {
        return profilePic;
    }

   /*  public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }*/

    public String getCommentUser() {
        return CommentUser;
    }

  /*   public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }*/
}
