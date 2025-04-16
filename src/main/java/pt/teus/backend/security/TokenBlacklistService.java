package pt.teus.backend.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, LocalDateTime> blacklistedTokens = new ConcurrentHashMap<>();

    // Add a token to the blacklist with its expiration time
    public void addToBlacklist(String token, LocalDateTime expiration) {
        blacklistedTokens.put(token, expiration);
    }

    // Check if a token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    // Clean up expired tokens from the blacklist
    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}

