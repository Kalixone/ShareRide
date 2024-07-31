package mate.academy.car_sharing_app.controller;

import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.service.TelegramNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final TelegramNotificationService notificationService;

    @GetMapping("/send")
    public ResponseEntity<String> sendNoticiation() {
        notificationService.sendNotification("test message");
        return ResponseEntity.ok("Notification send");
    }
}
