package com.example.demo.controller;

import static org.hamcrest.Matchers.hasItem;
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
    @DisplayName("최초 정책 조회는 차단 해제된 고정 확장자와 빈 커스텀 목록을 반환한다")
    void getsInitialPolicies() throws Exception {
        // given

        // when
        var result = mockMvc.perform(get("/api/v1/extension-policies"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fixed").isArray())
                .andExpect(jsonPath("$.fixed").isNotEmpty())
                .andExpect(jsonPath("$.fixed[0].extension").isNotEmpty())
                .andExpect(jsonPath("$.fixed[0].blocked").value(false))
                .andExpect(jsonPath("$.fixed[?(@.blocked == true)]").isEmpty())
                .andExpect(jsonPath("$.custom").isEmpty());
    }

    @Test
    @DisplayName("정책을 변경·등록·삭제하면 다음 정책 조회에 최신 상태가 반영된다")
    void getsLatestPoliciesAfterMutations() throws Exception {
        // given
        var changeResult = mockMvc.perform(patch("/api/v1/extension-policies/fixed/exe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"blocked\":true}"));
        var registerResult = mockMvc.perform(post("/api/v1/extension-policies/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"extension\":\"sh\"}"));

        // when
        var resultAfterMutation = mockMvc.perform(get("/api/v1/extension-policies"));
        var deleteResult = mockMvc.perform(delete("/api/v1/extension-policies/custom/sh"));
        var resultAfterDelete = mockMvc.perform(get("/api/v1/extension-policies"));

        // then
        changeResult.andExpect(status().isOk());
        registerResult.andExpect(status().isCreated());
        resultAfterMutation.andExpect(status().isOk())
                .andExpect(jsonPath("$.fixed[?(@.extension == 'exe')].blocked", hasItem(true)))
                .andExpect(jsonPath("$.custom", hasItem("sh")));
        deleteResult.andExpect(status().isNoContent());
        resultAfterDelete.andExpect(status().isOk())
                .andExpect(jsonPath("$.custom").isEmpty());
    }
}
