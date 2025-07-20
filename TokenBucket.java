import java.time.Instant;

// TokenBucket implements a rate-limiting mechanism using the Token Bucket algorithm. ~abhishek
public class TokenBucket {
    private final long capacity;             // Maximum number of tokens the bucket can hold
    private final double fillRate;           // Rate at which tokens are refilled (tokens per second)
    private double tokens;                   // Current number of tokens available in the bucket
    private Instant lastRefillTimestamp;     // Timestamp of the last refill

    // Constructor initializes the bucket with full tokens and sets capacity and refill rate
    public TokenBucket(long capacity, double fillRate) {
        this.capacity = capacity;               // Set max bucket capacity
        this.fillRate = fillRate;               // Set refill rate (tokens per second)
        this.tokens = capacity;                 // Bucket starts full
        this.lastRefillTimestamp = Instant.now(); // Set last refill time to now
    }

    /**
     * Checks if a request can be allowed by consuming 'tokens' number of tokens.
     * Returns true if allowed; false if not enough tokens.
     */
    public synchronized boolean allowRequest(int tokens) {
        refill();  // Update token count based on elapsed time since last refill

        // If we don't have enough tokens for this request, deny it
        if (this.tokens < tokens) {
            // For example: current tokens = 3, request = 5 → deny
            return false;
        }

        // Enough tokens are available; allow the request by deducting the tokens
        this.tokens -= tokens;

        return true; // Request is allowed
    }

    /**
     * Refill tokens based on time passed since last refill.
     * Doesn't exceed the maximum capacity.
     */
    private void refill() {
        Instant now = Instant.now(); // Current time

        // Time elapsed in milliseconds since last refill
        long elapsedTimeMillis = now.toEpochMilli() - lastRefillTimestamp.toEpochMilli();

        // Calculate tokens to add = elapsed time * rate (converted to per millisecond)
        double tokensToAdd = (elapsedTimeMillis * fillRate) / 1000.0;

        // Update token count but don't exceed capacity
        this.tokens = Math.min(capacity, this.tokens + tokensToAdd);

        // Update the timestamp to now
        this.lastRefillTimestamp = now;
    }
}
