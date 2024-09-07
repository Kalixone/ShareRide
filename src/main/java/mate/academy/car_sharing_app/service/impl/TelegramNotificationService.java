package mate.academy.car_sharing_app.service.impl;

import mate.academy.car_sharing_app.service.NotificationService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class TelegramNotificationService implements NotificationService {
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.chat.id}")
    private String chatId;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void sendNotification(String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        JSONObject payload = new JSONObject();
        payload.put("chat_id", chatId);
        payload.put("text", message);

        RequestBody body = RequestBody.create(payload.toString(),
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Notification sent successfully");
            } else {
                System.err.println("Failed to send notification, status code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Exception occurred: " + e.getMessage());
        }
    }
}
