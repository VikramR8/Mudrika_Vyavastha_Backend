package in.vikramaditya.MudrikaVyavastha.controllers;

import in.vikramaditya.MudrikaVyavastha.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<Map<String,Object>> getDashboardData() {
        Map<String,Object> dashboardData = dashboardService.getDashboard();
        return ResponseEntity.ok(dashboardData);
    }
}
