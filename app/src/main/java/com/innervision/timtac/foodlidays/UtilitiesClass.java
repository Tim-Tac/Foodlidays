package com.innervision.timtac.foodlidays;

public class UtilitiesClass {

    public static class Order_Article
    {
        String name;
        int quantity;
        String image;
        String prix;
        int id;
    }

    public static class Article
    {
        String name;
        String detail;
        String prix;
        String image;
        int id;
    }

    public static class Order
    {
        int id;
        String recap;
        String status;
        String time;
        String method_payement;
        String prix;
    }

}
