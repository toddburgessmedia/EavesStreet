package com.toddburgessmedia.eavesstreet.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/11/16.
 */

public class EAProfile implements Serializable {

    @SerializedName("ticker")
    String ticker;

    @SerializedName("full_name")
    String fullName;

    @SerializedName("joined")
    String joined;

    @SerializedName("close")
    double close;

    @SerializedName("close_money")
    double closeMoney;

    @SerializedName("yesterday_change")
    double change;

    @SerializedName("location")
    String location;

    @SerializedName("country")
    String country;

    @SerializedName("volume")
    int volume;

    @SerializedName("avg_div_per_share")
    double dividend;

    @SerializedName("investments_count")
    int investments;

    @SerializedName("shareholders_count")
    int shareholders;

    @SerializedName("lg_portrait")
    String portrait;

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getCloseMoney() {
        return closeMoney;
    }

    public void setCloseMoney(double closeMoney) {
        this.closeMoney = closeMoney;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getJoined() {

        return joined.substring(0,joined.indexOf(' '));
    }

    public double getDividend() {
        return dividend;
    }

    public void setDividend(double dividend) {
        this.dividend = dividend;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCountry() {
        return country;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getInvestments() {
        return investments;
    }

    public void setInvestments(int investments) {
        this.investments = investments;
    }

    public int getShareholders() {
        return shareholders;
    }

    public void setShareholders(int shareholders) {
        this.shareholders = shareholders;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    @Override
    public String toString() {
        return ticker + " " + fullName;
    }
}
