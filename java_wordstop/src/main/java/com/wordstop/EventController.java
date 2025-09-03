package com.wordstop;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;

@RestController
@RequestMapping("/events")
public class EventController {
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam(required = false) String channel) {
        SseEmitter emitter = new SseEmitter();
        
        executorService.execute(() -> {
            try {
                // Vulnerable: No authentication
                // Vulnerable: Information disclosure through SSE
                Random random = new Random();
                
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    // Leaks sensitive information through events
                    String data = String.format(
                        "{'user': 'admin', 'session': '%s', 'action': 'login', 'ip': '192.168.1.%d'}",
                        java.util.UUID.randomUUID(),
                        random.nextInt(255)
                    );
                    emitter.send(data);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }

    @PostMapping("/publish")
    public void publish(@RequestParam String channel, @RequestBody String message) {
        // Vulnerable: No authentication
        // Vulnerable: Cross-Site Scripting through SSE
        // Implementation would broadcast to all subscribers
    }

    @GetMapping("/debug")
    public SseEmitter debugEvents() {
        SseEmitter emitter = new SseEmitter();
        
        executorService.execute(() -> {
            try {
                // Vulnerable: Exposes system information through SSE
                emitter.send("System Info: " + System.getProperties());
                emitter.send("Environment: " + System.getenv());
                emitter.send("Memory: " + Runtime.getRuntime().totalMemory());
                emitter.send("Threads: " + Thread.activeCount());
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }
}
