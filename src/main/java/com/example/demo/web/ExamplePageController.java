package com.example.demo.web;

import com.example.demo.domain.ExampleEntity;
import com.example.demo.domain.ExampleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ExamplePageController {

    private final ExampleRepository exampleRepository;

    public ExamplePageController(final ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    @GetMapping("/")
    public String index(final Model model) {
        return renderIndex(model);
    }

    @PostMapping("/examples")
    public String create(final @RequestParam("name") String name, final Model model) {
        try {
            exampleRepository.save(ExampleEntity.create(name));
            return "redirect:/";
        } catch (final IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
            return renderIndex(model);
        }
    }

    private String renderIndex(final Model model) {
        model.addAttribute("examples", exampleRepository.findAll());
        return "index";
    }
}
