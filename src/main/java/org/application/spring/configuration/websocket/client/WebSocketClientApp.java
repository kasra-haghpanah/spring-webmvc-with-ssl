package org.application.spring.configuration.websocket.client;

import org.application.spring.configuration.exception.ApplicationException;
import org.application.spring.configuration.security.AuthenticationRequest;
import org.application.spring.configuration.security.AuthenticationResponse;
import org.application.spring.configuration.ssl.SslContextBuilder;
import org.application.spring.ddd.dto.ChatMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketClientApp {


    private static final SSLContext sslContext;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static volatile WebSocketSession currentSession;
    private static final RestClient restClient;
    private static String token;

    static {
        sslContext = SslContextBuilder.buildSslContext(
                "PKCS12",
                "classpath:p12/client.p12",
                "client123",
                "classpath:p12/client-truststore.p12",
                "trust123"
        );
        restClient = SslContextBuilder.createSecureRestClient(sslContext);
    }

    public static String login(String userName, String password) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(userName, password);

        AuthenticationResponse response = restClient.post()
                .uri("https://localhost:8443/spring/login")
                .header("Accept-Language", "fa")
                .body(authenticationRequest)
                .exchange((clientRequest, clientResponse) -> {

                    if (clientResponse.getStatusCode().isError()) {
                        throw new ApplicationException("url.invalid", HttpStatus.resolve(HttpStatus.BAD_REQUEST.value()), null);
                    }
                    clientResponse.getHeaders()
                            .forEach((key, values) -> {
                                if (values != null) {
                                    for (String value : values) {
                                        //response.addHeader(key, value);
                                    }
                                }
                            });
                    return clientResponse.bodyTo(AuthenticationResponse.class);
                });

        token = response.token();
        if (currentSession == null || !currentSession.isOpen()) {
            connect();
            refreshToken();
        }
        return token;
    }

    public static void refreshToken() {
        // اینجا می‌تونی از WebClient یا RestTemplate برای دریافت کوکی جدید استفاده کنی
        //return "access_token=newToken123; otherCookie=value";

        scheduler.scheduleAtFixedRate(() -> {

            AuthenticationResponse response = restClient.post()
                    .uri("https://localhost:8443/spring/refresh/token")
                    .headers(httpHeaders -> {
                        String cookie = MessageFormat.format("access_token={0}; from=sever", token);
                        httpHeaders.add("Accept-Language", "fa");
                        httpHeaders.add("Cookie", cookie);
                    })
                    //.body(authenticationRequest)
                    .exchange((clientRequest, clientResponse) -> {

                        if (clientResponse.getStatusCode().isError()) {
                            throw new ApplicationException("url.invalid", HttpStatus.resolve(HttpStatus.BAD_REQUEST.value()), null);
                        }
                        clientResponse.getHeaders()
                                .forEach((key, values) -> {
                                    if (values != null) {
                                        for (String value : values) {
                                            //response.addHeader(key, value);
                                        }
                                    }
                                });
                        return clientResponse.bodyTo(AuthenticationResponse.class);
                    });

            token = response.token();
            System.out.println("refreshToken: " + response.token());


        }, 0, 14, TimeUnit.MINUTES);

    }

    public static void sendMessageSafe(String message) {
        synchronized (currentSession) {
            try {
                String chat = new ChatMessage("application@gmail.com", "sender", message).toString();
                currentSession.sendMessage(new TextMessage(chat));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void connect() {


        try {
            // بررسی وضعیت اتصال
            if (currentSession == null || !currentSession.isOpen()) {
                System.out.println("🔄 اتصال قطع شده، در حال تلاش برای ری‌کانکت...");

                // دریافت کوکی جدید
                String newCookie = MessageFormat.format("access_token={0}; from=sever", token);

                // ساخت هدر جدید
                WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
                headers.add("Cookie", newCookie);

                // ساخت کلاینت جدید
                StandardWebSocketClient client = new StandardWebSocketClient();
                client.setSslContext(sslContext);

                // اتصال مجدد
                CompletableFuture<WebSocketSession> future = client.execute(
                        new ClientWebSocketHandler(),
                        headers,
                        URI.create("wss://localhost:8443/spring/ws")
                );

                future.thenAccept(session -> {
                    currentSession = session;
                    System.out.println("✅ اتصال مجدد برقرار شد");
                    try {
                        session.sendMessage(new TextMessage("سلام مجدد از کلاینت Spring!"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).exceptionally(ex -> {
                    System.err.println("❌ خطا در اتصال مجدد: " + ex.getMessage());
                    return null;
                });
            } else {
                System.out.println("✅ اتصال برقرار است، نیازی به ری‌کانکت نیست");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Exception {

        // ساخت WebSocketClient با SSL سفارشی
        StandardWebSocketClient client = new StandardWebSocketClient();
        client.setSslContext(sslContext);
        //client.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", sslContext);


        // ساخت هدرها
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        //headers.add("Authorization", "Bearer YOUR_ACCESS_TOKEN");
        headers.add("Cookie", "access_token=abc123; otherCookie=value");

        CompletableFuture<WebSocketSession> future = client.execute(
                new ClientWebSocketHandler(),
                headers,
                URI.create("wss://localhost:8443/spring/ws")
        );

        future.thenAccept(session -> {
            System.out.println("✅ اتصال برقرار شد");
            try {
                session.sendMessage(new TextMessage("سلام از کلاینت Spring!"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).exceptionally(ex -> {
            System.err.println("❌ خطا در اتصال: " + ex.getMessage());
            return null;
        });
    }


}

