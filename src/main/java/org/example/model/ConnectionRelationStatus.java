package org.example.model; // Or a new 'enums' package if you prefer

public enum ConnectionRelationStatus {
    NOT_CONNECTED,      // No connection exists
    PENDING_SENT,       // Current user sent a request, pending acceptance
    PENDING_RECEIVED,   // Current user received a request, pending acceptance
    ACCEPTED,           // Users are connected/friends
    REJECTED_BY_YOU,    // Current user rejected a request from this user
    REJECTED_BY_THEM,   // This user rejected a request from the current user
    BLOCKED_BY_YOU,     // Current user blocked this user
    BLOCKED_BY_THEM     // This user blocked the current user
}
