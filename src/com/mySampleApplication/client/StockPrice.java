package com.mySampleApplication.client;

/**
 * Created by kelale on 4/20/2015.
 */
public class StockPrice {

    private String symbol;
    private double price;
    private double change;

    public StockPrice(String symbol, double price, double change) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChangePercent() {
        return 100.0 * change / price;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChange() {
        return change;
    }
}
