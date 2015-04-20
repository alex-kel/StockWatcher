package com.mySampleApplication.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.DOM;

import java.util.Date;
import java.util.ArrayList;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class MySampleApplication implements EntryPoint {

    private static final int REFRESH_INTERVAL = 5000;
    private VerticalPanel mainPanel = new VerticalPanel();
    private FlexTable stocksFlexTable = new FlexTable();
    private HorizontalPanel addPanel = new HorizontalPanel();
    private TextBox newSymbolTextBox = new TextBox();
    private Button addStockButton = new Button("Add");
    private Label lastUpdatedLabel = new Label();
    private ArrayList<String> stocks = new ArrayList<String>();

    @Override
    public void onModuleLoad() {
        // Create table for stock data.
        stocksFlexTable.setText(0, 0, "Symbool");
        stocksFlexTable.setText(0, 1, "Price");
        stocksFlexTable.setText(0, 2, "Change");
        stocksFlexTable.setText(0, 3, "Remove");
        // Assemble Add Stock panel.
        addPanel.add(newSymbolTextBox);
        addPanel.add(addStockButton);
        // Assemble Main panel.
        mainPanel.add(stocksFlexTable);
        mainPanel.add(addPanel);
        mainPanel.add(lastUpdatedLabel);
        // Associate the Main panel with the HTML host page.
        RootPanel.get("stockList").add(mainPanel);
        //Move cursor focus to the input box.
        newSymbolTextBox.setFocus(true);

        addStockButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addStock();
            }
        });

        newSymbolTextBox.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    addStock();
                }
            }
        });

        Timer refreshTimer = new Timer() {
            @Override
            public void run() {
                refreshWatchList();
            }
        };
        refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
    }

    private void refreshWatchList() {
        final double MAX_PRICE = 100.0; // $100.00
        final double MAX_PRICE_CHANGE = 0.02; // +/- 2%

        StockPrice[] prices = new StockPrice[stocks.size()];
        for (int i = 0; i < stocks.size(); i++) {
            double price = Random.nextDouble() * MAX_PRICE;
            double change = price * MAX_PRICE_CHANGE
                    * (Random.nextDouble() * 2.0 - 1.0);

            prices[i] = new StockPrice(stocks.get(i), price, change);
        }

        updateTable(prices);
    }

    private void updateTable(StockPrice[] prices) {
        for (int i = 0; i < prices.length; i++) {
            updateTable(prices[i]);
        }
        DateTimeFormat dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        lastUpdatedLabel.setText("Last update : " + dateFormat.format(new Date()));
    }

    private void updateTable(StockPrice price) {
        if (!stocks.contains(price.getSymbol())) {
            return;
        }

        int row = stocks.indexOf(price.getSymbol()) + 1;
        String priceText = NumberFormat.getFormat("#,##0.00").format(price.getPrice());
        NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
        String changeText = changeFormat.format(price.getChange());
        String changePercentText = changeFormat.format(price.getChangePercent());

        stocksFlexTable.setText(row, 1, priceText);
        stocksFlexTable.setText(row, 2, changeText + " (" + changePercentText + "%)");
    }

    /**
     * Add stock to FlexTable. Executed when the user clicks the addStockButton or
     * presses enter in the newSymbolTextBox.
     */
    private void addStock() {
        final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
        newSymbolTextBox.setFocus(true);

        if (!symbol.matches("^[0-9A-Z&#92;&#92;.]{1,10}$")) {
            Window.alert("'" + symbol + "' is not a valid symbol");
            newSymbolTextBox.selectAll();
            return;
        }

        newSymbolTextBox.setText("");

        if (stocks.contains(symbol)) {
            return;
        }

        int row = stocksFlexTable.getRowCount();
        stocks.add(symbol);
        stocksFlexTable.setText(row, 0, symbol);

        Button removeStockButton = new Button("x");
        removeStockButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int removedIndex = stocks.indexOf(symbol);
                stocks.remove(removedIndex);
                stocksFlexTable.removeRow(removedIndex + 1);
            }
        });
        stocksFlexTable.setWidget(row, 3, removeStockButton);

        refreshWatchList();
    }
}
