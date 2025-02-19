package com.tete.example.equity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

//OrderBook class to manage buy and sell orders
class OrderBook {
    private final PriorityBlockingQueue<Order> buyOrders = new PriorityBlockingQueue<>(100, Comparator.comparingDouble(Order::getPrice).reversed().thenComparingLong(Order::getTimestamp));
    private final PriorityBlockingQueue<Order> sellOrders = new PriorityBlockingQueue<>(100, Comparator.comparingDouble(Order::getPrice).thenComparingLong(Order::getTimestamp));
    private final ReentrantLock lock = new ReentrantLock();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final File logFile = new File("trade_log.txt");
    
    public OrderBook() {
        try {
            if (!logFile.exists()) {
            	
            	// Ensure the log file exists
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Places a new order in the order book
    public void placeOrder(Order order) {
        executor.submit(() -> {
            lock.lock();
            try {
                if (order.getType() == Order.Type.BUY) {
                    buyOrders.offer(order);
                } else {
                    sellOrders.offer(order);
                }
                // Attempt to match orders after placing a new order
                matchOrders();
            } finally {
                lock.unlock();
            }
        });
    }
    
    // Matches buy and sell orders based on price and FIFO rules
    private void matchOrders() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buy = buyOrders.peek();
            Order sell = sellOrders.peek();
            
            if (buy.getPrice() >= sell.getPrice()) {
                int matchedQuantity = Math.min(buy.getQuantity(), sell.getQuantity());
                String logEntry = "Matched: " + matchedQuantity + " shares " + buy.getSymbol() + " @ " + sell.getPrice();
                System.out.println(logEntry);
                logTrade(logEntry); 
                
                buy.reduceQuantity(matchedQuantity);
                sell.reduceQuantity(matchedQuantity);
                
                // Remove fully matched buy order
                if (buy.getQuantity() == 0) buyOrders.poll();
                // Remove fully matched sell order
                if (sell.getQuantity() == 0) sellOrders.poll();
            } else {
            	// Stop matching if best buy price is lower than best sell price
                break;
            }
        }
    }
    
    // Logs matched trades to a file
    private void logTrade(String logEntry) {
        try (FileWriter fw = new FileWriter(logFile, true); BufferedWriter bw = new BufferedWriter(fw); PrintWriter out = new PrintWriter(bw)) {
            out.println(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}