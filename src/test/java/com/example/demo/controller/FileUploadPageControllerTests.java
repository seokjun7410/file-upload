package com.example.demo.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileUploadPageController.class)
class FileUploadPageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("파일 업로드 페이지는 서버 모델 없이 렌더링된다")
    void rendersFileUploadPageWithoutModel() throws Exception {
        // given

        // when
        var result = mockMvc.perform(get("/"));

        // then
        result.andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("id=\"fixed-policy-list\"")))
                .andExpect(content().string(containsString("id=\"custom-policy-list\"")))
                .andExpect(model().size(0));
    }
}
