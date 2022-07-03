package com.project.chatter.service.impl;

import com.project.chatter.model.dto.UserDetailsDto;
import com.project.chatter.model.entity.ChatRoom;
import com.project.chatter.model.entity.Message;
import com.project.chatter.model.entity.User;
import com.project.chatter.model.enums.ChatRoomType;
import com.project.chatter.model.view.chat.ChatDetailsView;
import com.project.chatter.model.view.chat.ChatView;
import com.project.chatter.model.view.chat.MessageView;
import com.project.chatter.repository.ChatRoomRepository;
import com.project.chatter.repository.MessageRepository;
import com.project.chatter.repository.UserRepository;
import com.project.chatter.service.ChatService;
import com.project.chatter.web.exception.NotFoundError;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    public ChatServiceImpl(UserRepository userRepository, ChatRoomRepository chatRoomRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public void populateChatRoomAndMessages() {
        if (chatRoomRepository.count() == 0) {
            User user1 = userRepository.findById((long) 1).orElseThrow();
            User user2 = userRepository.findById((long) 2).orElseThrow();
            User user3 = userRepository.findById((long) 3).orElseThrow();

            ChatRoom chatRoom = new ChatRoom(ChatRoomType.PERSONAL, List.of(user1, user2), new ArrayList<>());
            ChatRoom chatRoom2 = new ChatRoom(ChatRoomType.PERSONAL, List.of(user1, user3), new ArrayList<>());
            chatRoomRepository.save(chatRoom);
            chatRoomRepository.save(chatRoom2);

            user1.setChatRooms(List.of(chatRoom, chatRoom2));
            user2.setChatRooms(List.of(chatRoom));
            user3.setChatRooms(List.of(chatRoom2));

            List<Message> messages = List.of(
                    new Message("Lorem Ipsum is simply dummy text of the printing and typesetting industry.", user1, chatRoom, timestamp()),
                    new Message("Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.", user1, chatRoom, timestamp()),
                    new Message("It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.", user2, chatRoom, timestamp()),
                    new Message("It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.", user3, chatRoom2, timestamp()),
                    new Message("It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages.", user1, chatRoom, timestamp()),
                    new Message("It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages.", user2, chatRoom, timestamp()),
                    new Message("It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages.", user3, chatRoom2, timestamp())
            );
            messageRepository.saveAll(messages);
        }
    }

    private static LocalDateTime timestamp() {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(ThreadLocalRandom.current().nextInt()), ZoneOffset.UTC);
    }
