package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.ExtensionPolicy;
import com.example.demo.exception.CustomExtensionLimitExceededException;
import com.example.demo.exception.CustomExtensionPolicyNotFoundException;
import com.example.demo.exception.DuplicateExtensionPolicyException;
import com.example.demo.exception.FixedExtensionPolicyNotFoundException;
import com.example.demo.exception.InvalidExtensionException;
import com.example.demo.service.ExtensionPolicyService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExtensionPolicyRestController.class)
@Import(ExtensionPolicyRestExceptionHandler.class)
class ExtensionPolicyRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExtensionPolicyService service;

    @Test
    @DisplayName("정책 조회 API는 고정 정책과 커스텀 정책을 계약된 JSON으로 반환한다")
    void getsExtensionPolicies() throws Exception {
        // given
        when(service.findAll()).thenReturn(List.of(
                ExtensionPolicy.fixed("bat"),
                ExtensionPolicy.fixed("cmd"),
                ExtensionPolicy.fixed("com"),
                ExtensionPolicy.fixed("cpl"),
                ExtensionPolicy.fixed("exe"),
                ExtensionPolicy.fixed("scr"),
                ExtensionPolicy.fixed("js"),
                ExtensionPolicy.custom("php"),
                ExtensionPolicy.custom("sh")
        ));

        // when
        var result = mockMvc.perform(get("/api/v1/extension-policies"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fixed").isArray())
                .andExpect(jsonPath("$.fixed").value(org.hamcrest.Matchers.hasSize(7)))
                .andExpect(jsonPath("$.fixed[0].extension").value("bat"))
                .andExpect(jsonPath("$.fixed[0].blocked").value(false))
                .andExpect(jsonPath("$.custom").isArray())
                .andExpect(jsonPath("$.custom", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.custom[0]").value("php"))
                .andExpect(jsonPath("$.custom[1]").value("sh"));
    }

    @Test
    @DisplayName("고정 정책 변경 API는 변경된 정책을 200 응답으로 반환한다")
    void changesFixedPolicy() throws Exception {
        // given
        ExtensionPolicy changedPolicy = ExtensionPolicy.fixed("exe");
        changedPolicy.changeBlocked(true);
        when(service.changeFixedBlocked(eq("exe"), eq(true))).thenReturn(changedPolicy);

        // when
        var result = mockMvc.perform(patch("/api/v1/extension-policies/fixed/exe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\":true}"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.extension").value("exe"))
                .andExpect(jsonPath("$.blocked").value(true));
    }

    @Test
    @DisplayName("커스텀 정책 등록 API는 정규화된 확장자를 201 응답으로 반환한다")
    void registersCustomPolicy() throws Exception {
        // given
        when(service.registerCustom(" SH ")).thenReturn(ExtensionPolicy.custom("sh"));

        // when
        var result = mockMvc.perform(post("/api/v1/extension-policies/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"extension\":\" SH \"}"));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.extension").value("sh"));
    }

    @Test
    @DisplayName("커스텀 정책 삭제 API는 본문 없는 204 응답을 반환한다")
    void deletesCustomPolicy() throws Exception {
        // given
        doNothing().when(service).deleteCustom("SH");

        // when
        var result = mockMvc.perform(delete("/api/v1/extension-policies/custom/SH"));

        // then
        result.andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("빈 확장자 요청은 INVALID_EXTENSION 오류를 반환한다")
    void rejectsBlankExtension() throws Exception {
        // given
        when(service.registerCustom(" "))
                .thenThrow(new InvalidExtensionException("extension must not be blank"));

        // when
        var result = mockMvc.perform(post("/api/v1/extension-policies/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"extension\":\" \"}"));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_EXTENSION"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("필수 필드가 없는 고정 정책 변경 요청은 INVALID_REQUEST 오류를 반환한다")
    void rejectsMissingBlockedField() throws Exception {
        // given

        // when
        var result = mockMvc.perform(patch("/api/v1/extension-policies/fixed/exe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("필수 필드가 없는 커스텀 정책 등록 요청은 INVALID_REQUEST 오류를 반환한다")
    void rejectsMissingExtensionField() throws Exception {
        // given

        // when
        var result = mockMvc.perform(post("/api/v1/extension-policies/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("이미 등록된 확장자 요청은 DUPLICATE_EXTENSION 오류를 반환한다")
    void rejectsDuplicateExtension() throws Exception {
        // given
        when(service.registerCustom("exe"))
                .thenThrow(new DuplicateExtensionPolicyException("extension policy already exists"));

        // when
        var result = mockMvc.perform(post("/api/v1/extension-policies/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"extension\":\"exe\"}"));

        // then
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EXTENSION"));
    }

    @Test
    @DisplayName("커스텀 최대 개수 초과 요청은 CUSTOM_LIMIT_EXCEEDED 오류를 반환한다")
    void rejectsCustomLimitExceeded() throws Exception {
        // given
        when(service.registerCustom("overflow"))
                .thenThrow(new CustomExtensionLimitExceededException("custom extension policy limit exceeded"));

        // when
        var result = mockMvc.perform(post("/api/v1/extension-policies/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"extension\":\"overflow\"}"));

        // then
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CUSTOM_LIMIT_EXCEEDED"));
    }

    @Test
    @DisplayName("없는 고정 정책 변경 요청은 FIXED_EXTENSION_NOT_FOUND 오류를 반환한다")
    void rejectsUnknownFixedPolicy() throws Exception {
        // given
        when(service.changeFixedBlocked(eq("unknown"), anyBoolean()))
                .thenThrow(new FixedExtensionPolicyNotFoundException("fixed extension policy not found"));

        // when
        var result = mockMvc.perform(patch("/api/v1/extension-policies/fixed/unknown")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\":true}"));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("FIXED_EXTENSION_NOT_FOUND"));
    }

    @Test
    @DisplayName("없는 커스텀 정책 삭제 요청은 CUSTOM_EXTENSION_NOT_FOUND 오류를 반환한다")
    void rejectsUnknownCustomPolicy() throws Exception {
        // given
        doThrow(new CustomExtensionPolicyNotFoundException(
                        "custom extension policy not found"
                ))
                .when(service)
                .deleteCustom("missing");

        // when
        var result = mockMvc.perform(delete("/api/v1/extension-policies/custom/missing"));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CUSTOM_EXTENSION_NOT_FOUND"));
    }

    @Test
    @DisplayName("서비스가 잘못된 확장자를 거부하면 INVALID_EXTENSION 오류를 반환한다")
    void mapsInvalidExtensionException() throws Exception {
        // given
        when(service.registerCustom("tar.gz"))
                .thenThrow(new InvalidExtensionException("extension must not contain a dot"));

        // when
        var result = mockMvc.perform(post("/api/v1/extension-policies/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"extension\":\"tar.gz\"}"));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_EXTENSION"));
    }
}
