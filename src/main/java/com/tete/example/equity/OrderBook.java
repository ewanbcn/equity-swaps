package com.tete.example.equity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

class OrderBook {
    private final PriorityQueue<Order> buyOrders = new PriorityQueue<>(Comparator.comparingDouble(Order::getPrice).reversed().thenComparingLong(Order::getTimestamp));
    private final PriorityQueue<Order> sellOrders = new PriorityQueue<>(Comparator.comparingDouble(Order::getPrice).thenComparingLong(Order::getTimestamp));
    private final ReentrantLock lock = new ReentrantLock();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final File logFile = new File("trade_log.txt");
    
    public OrderBook() {
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void placeOrder(Order order) {
        executor.submit(() -> {
            lock.lock();
            try {
                if (order.getType() == Order.Type.BUY) {
                    buyOrders.offer(order);
                } else {
                    sellOrders.offer(order);
                }
                matchOrders();
            } finally {
                lock.unlock();
            }
        });
    }
    
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
                
                if (buy.getQuantity() == 0) buyOrders.poll();
                if (sell.getQuantity() == 0) sellOrders.poll();
            } else {
                break;
            }
        }
    }
    
    private void logTrade(String logEntry) {
        try (FileWriter fw = new FileWriter(logFile, true); BufferedWriter bw = new BufferedWriter(fw); PrintWriter out = new PrintWriter(bw)) {
            out.println(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}