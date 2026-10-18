package ru.bdayloop.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.bdayloop.service.i.MessageService;
import ru.bdayloop.web.AuthUser;
import ru.bdayloop.web.dto.request.SendMessageRequest;
import ru.bdayloop.web.dto.response.MessageResponse;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse send(@RequestBody SendMessageRequest body, AuthUser user){
        return MessageResponse.from(messageService.sendMessage(body.subjectId(), user.id(), body.text()));
    }

    @GetMapping
    public List<MessageResponse> list(@RequestParam int subjectId, AuthUser user){
        return messageService.getMessages(subjectId, user.id()).stream().map(m -> MessageResponse.from(m)).toList();
    }
}
