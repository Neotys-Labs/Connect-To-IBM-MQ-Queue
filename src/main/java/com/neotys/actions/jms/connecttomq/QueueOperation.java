package com.neotys.actions.jms.connecttomq;

public enum QueueOperation {
    CREATE, // Create queue session then create queue.
    ACCESS; // Access existing queue MQQueueManager.
}