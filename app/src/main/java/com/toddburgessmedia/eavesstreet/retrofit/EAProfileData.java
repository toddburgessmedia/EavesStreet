package com.toddburgessmedia.eavesstreet.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/11/16.
 */

public class EAProfileData implements Serializable {

    @SerializedName("data")
    ArrayList<EAProfile> data;

    public ArrayList<EAProfile> getData() {
        return data;
    }

    public void setData(ArrayList<EAProfile> data) {
        this.data = data;
    }

    public EAProfile getProfile() {
        return data.get(0);
    }

    @Override
    public String toString() {

        return data.get(0).toString();
    }
}
