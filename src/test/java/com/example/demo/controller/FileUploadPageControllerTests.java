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
                .andExpect(model().size(0));
    }

    @Test
    @DisplayName("정책 화면은 REST API 조회 결과를 표시할 영역과 Axios 스크립트를 제공한다")
    void rendersPolicyContainersAndScripts() throws Exception {
        // given

        // when
        var result = mockMvc.perform(get("/"));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"policy-load-status\"")))
                .andExpect(content().string(containsString("id=\"fixed-policy-list\"")))
                .andExpect(content().string(containsString("id=\"custom-policy-list\"")))
                .andExpect(content().string(containsString(
                        "src=\"/webjars/axios/1.8.4/dist/axios.min.js\""
                )))
                .andExpect(content().string(containsString(
                        "src=\"/js/extension-policy.js\""
                )));
    }

    @Test
    @DisplayName("정책 화면에 필요한 Axios와 화면 스크립트를 정적 리소스로 제공한다")
    void servesPolicyPageScripts() throws Exception {
        // given

        // when
        var axiosResult = mockMvc.perform(get("/webjars/axios/1.8.4/dist/axios.min.js"));
        var policyScriptResult = mockMvc.perform(get("/js/extension-policy.js"));

        // then
        axiosResult.andExpect(status().isOk());
        policyScriptResult.andExpect(status().isOk());
    }
}
