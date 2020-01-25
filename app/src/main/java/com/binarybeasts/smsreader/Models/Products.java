package com.binarybeasts.smsreader.Models;

public class Products {
    String ProductName;
    String Price;
    String Location;
    String Quality;
    String Delivery;
    String PhoneNo;
    String Contact;
    String img;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;

    public Products() {
    }

    public Products(String productName, String price, String location, String quality, String delivery, String phoneNo, String contact, String img) {
        ProductName = productName;
        Price = price;
        Location = location;
        Quality = quality;
        Delivery = delivery;
        PhoneNo = phoneNo;
        Contact = contact;
        this.img = img;
    }

    public Products(String productName, String price, String location, String quality, String delivery, String phoneNo, String contact) {
        ProductName = productName;
        Price = price;
        Location = location;
        Quality = quality;
        Delivery = delivery;
        PhoneNo = phoneNo;
        Contact = contact;
    }

    public Products(String productName, String price, String location, String quality, String delivery, String phoneNo) {
        ProductName = productName;
        Price = price;
        Location = location;
        Quality = quality;
        Delivery = delivery;
        PhoneNo = phoneNo;
        Contact = "NA";
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getQuality() {
        return Quality;
    }

    public void setQuality(String quality) {
        Quality = quality;
    }

    public String getDelivery() {
        return Delivery;
    }

    public void setDelivery(String delivery) {
        Delivery = delivery;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }
}


// ^SELL{1}[\n]{1}((([a-zA-Z]+)[ ]+([a-zA-Z0-9]+))[\n]*){2}
