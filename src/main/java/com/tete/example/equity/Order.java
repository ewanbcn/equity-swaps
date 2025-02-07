package com.tete.example.equity;

class Order {
    enum Type { BUY, SELL }
    
    private final String symbol;
    private int quantity;
    private final double price;
    private final Type type;
    private final long timestamp;
    
    public Order(String symbol, int quantity, double price, Type type) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.timestamp = System.nanoTime();
    }
    
    public double getPrice() { return price; }
    public Type getType() { return type; }
    public int getQuantity() { return quantity; }
    public String getSymbol() { return symbol; }
    public long getTimestamp() { return timestamp; }
    
    public void reduceQuantity(int amount) {
        this.quantity -= amount;
    }
    
    @Override
    public String toString() {
        return "Order{" + "symbol='" + symbol + '\'' + ", quantity=" + quantity + ", price=" + price + ", type=" + type + "}";
    }
}
