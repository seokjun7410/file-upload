package com.example.demo.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:extension-policy-integration;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class ExtensionPolicyApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("최초 정책 조회는 차단 해제된 고정 확장자 7개와 빈 커스텀 목록을 반환한다")
    void getsInitialPolicies() throws Exception {
        // given

        // when
        var result = mockMvc.perform(get("/api/v1/extension-policies"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fixed", hasSize(7)))
                .andExpect(jsonPath("$.fixed[*].blocked", hasSize(7)))
                .andExpect(jsonPath("$.fixed[?(@.blocked == true)]").isEmpty())
                .andExpect(jsonPath("$.custom").isEmpty());
    }

    @Test
    @DisplayName("고정 확장자 차단 상태를 변경하면 다음 정책 조회에 즉시 반영된다")
    void getsChangedFixedPolicyImmediately() throws Exception {
        // given
        mockMvc.perform(patch("/api/v1/extension-policies/fixed/exe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"blocked\":true}"))
                .andExpect(status().isOk());

        // when
        var result = mockMvc.perform(get("/api/v1/extension-policies"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fixed[4].extension").value("exe"))
                .andExpect(jsonPath("$.fixed[4].blocked").value(true));
    }

    @Test
    @DisplayName("커스텀 확장자를 등록하면 다음 정책 조회에 즉시 반영된다")
    void getsRegisteredCustomPolicyImmediately() throws Exception {
        // given
        mockMvc.perform(post("/api/v1/extension-policies/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"extension\":\"sh\"}"))
                .andExpect(status().isCreated());

        // when
        var result = mockMvc.perform(get("/api/v1/extension-policies"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.custom", hasItem("sh")));
    }

    @Test
    @DisplayName("커스텀 확장자를 삭제하면 다음 정책 조회에서 즉시 제외된다")
    void excludesDeletedCustomPolicyImmediately() throws Exception {
        // given
        mockMvc.perform(post("/api/v1/extension-policies/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"extension\":\"php\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(delete("/api/v1/extension-policies/custom/php"))
                .andExpect(status().isNoContent());

        // when
        var result = mockMvc.perform(get("/api/v1/extension-policies"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.custom").isEmpty());
    }
}
