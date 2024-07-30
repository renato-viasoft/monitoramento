package br.com.viasoft.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class AppController {
    @GetMapping("/")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/ping/{key}")
    public ResponseEntity<?> ping(
        @PathVariable("key") String key
    ) {
        int randInt = (int) (Math.random() * 100) + 1;

        if (randInt <= 10) {
            return ResponseEntity.internalServerError().build();
        }
        if (randInt <= 25) {
            return ResponseEntity.badRequest().build();
        }

        if (randInt <= 45) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("PONG");
    }
}
