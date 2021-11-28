package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.domain.Views;
import com.example.demo.dto.MessagePageDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

import static com.example.demo.controller.MessageController.MESSAGES_PAGE_PER;

@Controller
@RequestMapping("/")
public class MainController {
    private final MessageService messageService;
    private final UserRepository userRepository;

    @Value("${spring.profiles.active}")
    private String profile;
    private final ObjectWriter writer;
    private final ObjectWriter profileWriter;

    @Autowired
    public MainController(MessageService messageService, UserRepository userRepository, ObjectMapper mapper) {
        this.messageService = messageService;
        this.userRepository = userRepository;

        ObjectMapper objectMapper = mapper
                .setConfig(mapper.getSerializationConfig());

        this.writer = objectMapper
                .writerWithView(Views.FullMessage.class);
        this.profileWriter = objectMapper
                .writerWithView(Views.FullProfile.class);
    }

    @GetMapping
    public String main(
            Model model,
            @AuthenticationPrincipal User user
    ) throws JsonProcessingException {
        HashMap<Object, Object> data = new HashMap<>();

        if(user != null) {
            User userFromDb = userRepository.findById(user.getId()).get();
            String serializedProfile = profileWriter.writeValueAsString(userFromDb);
            model.addAttribute("profile", serializedProfile);

            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            PageRequest pageRequest = PageRequest.of(0, MESSAGES_PAGE_PER, sort);
            MessagePageDto messagePageDto = messageService.findForUser(pageRequest, user);

            String messages = writer.writeValueAsString(messagePageDto.getMessages());

            model.addAttribute("messages", messages);
            data.put("currentPage", messagePageDto.getCurrentPage());
            data.put("totalPage", messagePageDto.getTotalPage());
        } else {
            model.addAttribute("messages", "[]");
            model.addAttribute("profile", "null");
        }
        model.addAttribute("frontendData", data);
        model.addAttribute("isDevMode", "dev". equals(profile));
        return "index";
    }
}
