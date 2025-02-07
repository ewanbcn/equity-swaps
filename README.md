# equity-swaps
 
### How It Works
* Orders are placed (BUY or SELL).
* Orders are stored in separate PriorityQueues (sorted for efficient matching).
* Matching Logic:
    * If the highest BUY price is greater than or equal to the lowest SELL price, a trade occurs.
    * The matched orders are removed from the queue.
    
## Supports
* ✅ Partial fills - Orders are reduced in quantity instead of being removed outright.
* ✅ FIFO ordering - Orders are processed in order of time-stamp when prices are equal.
* ✅ Multi-threading optimisations - Orders are processed asynchronously using an ExecutorService