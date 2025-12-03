package in.vikramaditya.MudrikaVyavastha.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0")
public class HealthController {

    @GetMapping("/keep-alive")
    public ResponseEntity<String> keepAlive() {
        // Just a simple string response. fast and cheap.
        return ResponseEntity.ok("Render is active");
    }
}