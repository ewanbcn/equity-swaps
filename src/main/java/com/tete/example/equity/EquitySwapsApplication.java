package com.tete.example.equity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EquitySwapsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EquitySwapsApplication.class, args);
		
		OrderBook orderBook = new OrderBook();
        
        orderBook.placeOrder(new Order("AAPL", 100, 185.50, Order.Type.BUY));
        orderBook.placeOrder(new Order("AAPL", 50, 185.40, Order.Type.SELL));
        orderBook.placeOrder(new Order("AAPL", 150, 185.60, Order.Type.BUY));
        orderBook.placeOrder(new Order("AAPL", 100, 185.50, Order.Type.SELL));
	}

}
