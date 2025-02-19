
# equity-swaps

## Data Structures

### HashMap & ConcurrentHashMap

Use case: Storing real-time price updates for stocks. Example:

```java
Map<String, Double> stockPrices = new HashMap<>();
stockPrices.put("AAPL", 185.75);
```

ConcurrentHashMap is a thread-safe alternative, useful for multi-threaded environments in trading systems.

### PriorityQueue

Use case: Order book implementation (sorting buy/sell orders by price/time). Example:

```java
PriorityQueue<Order> buyOrders = new PriorityQueue<>(Comparator.comparingDouble(Order::getPrice).reversed());
buyOrders.add(new Order("AAPL", 100, 185.50));
```

### ArrayList vs. LinkedList

* ArrayList is preferable for storing historical trade data because of fast random access.
* LinkedList can be used for real-time trade processing where frequent insertions/deletions occur.

### TreeMap

Use case: Managing an order book efficiently (sorted by price levels). Example:

```java
NavigableMap<Double, Order> orderBook = new TreeMap<>();
orderBook.put(185.50, new Order("AAPL", 100, 185.50));
```

## Concurrency

### Threads & Executors
Use case: Running parallel computations for risk analysis. Example:

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.submit(() -> processTrade("AAPL", 100, 185.75));
```

### Locks (ReentrantLock)

Use case: Preventing race conditions when multiple threads update the order book. Example:

```java
private final ReentrantLock lock = new ReentrantLock();

public void updateOrderBook(Order order) {
    lock.lock();
    try {
        // Modify order book
    } finally {
        lock.unlock();
    }
}
```

### Atomic Variables

Use case: Managing counter updates for executed trades in a thread-safe manner. Example:

```java
private final AtomicInteger tradeCounter = new AtomicInteger(0);
tradeCounter.incrementAndGet();
```

### CompletableFuture

Use case: Handling asynchronous tasks, such as fetching live market data. Example:

```java
CompletableFuture.supplyAsync(() -> fetchMarketData("AAPL"))
                 .thenAccept(data -> processMarketData(data));
```

## High Level architecture

* Market Data Handler → Uses ConcurrentHashMap for real-time stock prices.
* Order Matching Engine → Uses PriorityBlockingQueue and TreeMap for efficient order processing.
* Risk Management Module → Uses AtomicInteger and CompletableFuture for fast risk calculations.
* Trade Execution Module → Uses ExecutorService for concurrent execution of trade orders.

By leveraging Java’s efficient data structures and concurrency mechanisms, an equity swaps trading system can achieve low-latency execution, scalability, and high throughput.

### How It Works

* Orders are placed (BUY or SELL).
* Orders are stored in separate PriorityQueues (sorted for efficient matching).
* Matching Logic:
    * If the highest BUY price is greater than or equal to the lowest SELL price, a trade occurs.
    * The matched orders are removed from the queue.
    
### Supports

*  Partial fills - Orders are reduced in quantity instead of being removed outright.
*  FIFO ordering - Orders are processed in order of time-stamp when prices are equal.
*  Multi-threading optimisations - Orders are processed asynchronously using an ExecutorService