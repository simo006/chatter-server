package com.project.chatter.web.controller;

import com.project.chatter.model.dto.RegisterUserDto;
import com.project.chatter.model.dto.StatusChangeDto;
import com.project.chatter.model.view.UserDataView;
import com.project.chatter.model.view.basic.SuccessView;
import com.project.chatter.service.AuthService;
import com.project.chatter.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    private final AuthService authService;
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public AuthController(AuthService authService, UserService userService, SimpMessagingTemplate simpMessagingTemplate) {
        this.authService = authService;
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessView> register(@Valid @RequestBody RegisterUserDto registerUserDto,
                                                BindingResult bindingResult, HttpServletRequest request) throws ServletException {
        if (bindingResult.hasErrors()) {
            throwRequestBodyValidationError(bindingResult);
        }

        authService.register(registerUserDto);

        request.login(registerUserDto.getEmail(), registerUserDto.getPassword());

        UserDataView userDataView = new UserDataView(registerUserDto.getEmail(), registerUserDto.getFirstName(),
                registerUserDto.getLastName(), registerUserDto.getAge());

        return ResponseEntity.ok(okView("Successful registration", userDataView));
    }

    @GetMapping("/user-chat-rooms")
    public ResponseEntity<SuccessView> getUserChatRooms(Principal principal) {
        List<String> userChatRooms = userService.getUserChatRooms(principal.getName());

        return ResponseEntity.ok(okView("User chat rooms", userChatRooms));
    }

    @MessageMapping("/friends/status")
    public void sendStatusNotificationToAllFriends(Principal principal, @Payload @Valid Message<StatusChangeDto> message) {
        List<String> friendsEmails = userService.getUserFriendsEmails(principal.getName());

        // Send status to all friends that the current user is online
        friendsEmails.forEach(email -> simpMessagingTemplate.convertAndSendToUser(email, "/queue/status", message));
    }

    @MessageMapping("/friend/status")
    public void sendStatusNotificationToFriend(@Payload @Valid Message<StatusChangeDto> message) {
        StatusChangeDto statusChangeDto = message.getPayload();

        // Return status back to the user the other user is online and has received the previous message
        simpMessagingTemplate.convertAndSendToUser(statusChangeDto.getSendTo(), "/queue/status", message);
    }
}
