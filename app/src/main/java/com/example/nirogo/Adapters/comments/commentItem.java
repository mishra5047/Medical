package com.example.nirogo.Adapters.comments;

public class commentItem {
    String username;
    String usercomment;

    public commentItem(String username,String usercomment)
    {
        this.username= username;
        this.usercomment=usercomment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getusercomment() {
        return usercomment;
    }

    public void setusercomment(String usercomment) {
        this.usercomment= usercomment;
    }

}
