package com.example.sai.newsapp;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sai on 17-06-2018.
 */
public class ImageUploadInfo extends ArrayList<ImageUploadInfo> {

        public String imageName;

        public String imageURL;

        public String category;

        public String email;


    public ImageUploadInfo()

        {

        }



    public ImageUploadInfo(String name, String url) {

            this.imageName = name;
            this.imageURL= url;

        }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ImageUploadInfo(String name, String url, String category, String email) {

        this.imageName = name;
        this.imageURL= url;
        this.category=category;
        this.email=email;

    }

        public String getImageName() {
            return imageName;
        }

        public String getImageURL() {
            return imageURL;
        }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImageURL(String  imageURL) {
        this.imageURL = imageURL;
    }


}

