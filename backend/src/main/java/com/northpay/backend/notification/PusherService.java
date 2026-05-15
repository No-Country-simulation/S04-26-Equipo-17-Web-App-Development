package com.northpay.backend.notification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.northpay.backend.common.config.LocalDateTimeAdapter;
import com.northpay.backend.common.config.PusherConfig;
import com.pusher.rest.Pusher;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PusherService {

    private final PusherConfig config;
    private Pusher pusher;

    public PusherService(PusherConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        this.pusher = new Pusher(config.appId(), config.key(), config.secret());
        this.pusher.setCluster(config.cluster());
        this.pusher.setEncrypted(true);
        this.pusher.setGsonSerialiser(gson);

        log.info("Pusher inicializado: app={} cluster={}", config.appId(), config.cluster());
    }

    public void notifyOperators(PusherEvent event) {
        String channel = "private-onboarding";

        try {
            pusher.trigger(channel, event.type(), event);
            log.info("Evento Pusher enviado: canal={} tipo={} onboardingId={}",
                    channel, event.type(), event.onboardingId());
        } catch (Exception e) {
            log.error("Error al enviar evento Pusher: canal={} tipo={}", channel, event.type(), e);
        }
    }
}