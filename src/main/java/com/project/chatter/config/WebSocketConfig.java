package com.project.chatter.config;

import com.project.chatter.config.interceptor.ChatRoomChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatRoomChannelInterceptor chatRoomChannelInterceptor;

    public WebSocketConfig(ChatRoomChannelInterceptor chatRoomChannelInterceptor) {
        this.chatRoomChannelInterceptor = chatRoomChannelInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/chat-room", "/friends", "/friend", "/queue");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Fallback endpoint if the websocket protocol is not used
        registry.addEndpoint("/chat")
                .setAllowedOrigins("http://localhost:8080");

        registry.addEndpoint("/chat")
                .setAllowedOrigins("http://localhost:8080")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Validate if the user can subscribe to the desired chat room
        registration.interceptors(chatRoomChannelInterceptor);
    }
}
