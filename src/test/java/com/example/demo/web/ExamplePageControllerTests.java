package com.example.demo.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.demo.domain.ExampleEntity;
import com.example.demo.domain.ExampleRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExamplePageController.class)
class ExamplePageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExampleRepository exampleRepository;

    @Test
    void displaysExamplesOnRootPage() throws Exception {
        given(exampleRepository.findAll()).willReturn(List.of(ExampleEntity.create("demo")));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
