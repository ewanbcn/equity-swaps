package com.tete.example.equity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderBookTest {
    private OrderBook orderBook;
    private File logFile;

    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
        logFile = new File("trade_log.txt");
    }

    @AfterEach
    void cleanUp() {
    	// Clean up log file after tests
        logFile.delete();
    }

    // Test matching of a buy and sell order at the same price
    @Test
    void testOrderMatching() throws IOException {
        orderBook.placeOrder(new Order("AAPL", 100, 185.50, Order.Type.BUY));
        orderBook.placeOrder(new Order("AAPL", 100, 185.50, Order.Type.SELL));
        
        waitForExecution();
        
        assertTrue(logFile.exists(), "Trade log should be created");
        List<String> lines = Files.readAllLines(logFile.toPath());
        assertEquals(1, lines.size(), "One trade should be logged");
        assertTrue(lines.get(0).contains("100 shares AAPL @ 185.5"), "Trade details should match");
        
    }

    // Test partial order fulfilment when buy quantity > sell quantity
	@Test
    void testPartialFill() throws IOException {
        orderBook.placeOrder(new Order("AAPL", 150, 185.50, Order.Type.BUY));
        orderBook.placeOrder(new Order("AAPL", 100, 185.50, Order.Type.SELL));
        
        waitForExecution();
        
        assertTrue(logFile.exists());
        List<String> lines = Files.readAllLines(logFile.toPath());
        assertEquals(1, lines.size(), "One trade should be logged");
        assertTrue(lines.get(0).contains("100 shares AAPL @ 185.5"), "Partial trade should be logged");
    }
	
	// Test FIFO ordering
    @Test
    void testFIFOOrdering() throws IOException {
        orderBook.placeOrder(new Order("AAPL", 50, 185.50, Order.Type.BUY));
        orderBook.placeOrder(new Order("AAPL", 50, 185.50, Order.Type.BUY));
        orderBook.placeOrder(new Order("AAPL", 100, 185.50, Order.Type.SELL));
        
        waitForExecution();
        
        List<String> lines = Files.readAllLines(logFile.toPath());
        assertEquals(2, lines.size(), "Two trades should be logged in FIFO order");
        assertTrue(lines.get(0).contains("50 shares AAPL @ 185.5"));
        assertTrue(lines.get(1).contains("50 shares AAPL @ 185.5"));
    }

    // Test concurrency handling
    @Test
    void testConcurrencyHandling() throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> orderBook.placeOrder(new Order("AAPL", 10, 185.50, Order.Type.BUY)));
            executor.submit(() -> orderBook.placeOrder(new Order("AAPL", 10, 185.50, Order.Type.SELL)));
        }
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);
        
        waitForExecution();
        
        List<String> lines = Files.readAllLines(logFile.toPath());
        assertEquals(10, lines.size(), "All 10 trades should be logged");
    }
     
    private void waitForExecution() {
		try {
			Thread.sleep(500); // Wait for async execution
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
		
	}
}

