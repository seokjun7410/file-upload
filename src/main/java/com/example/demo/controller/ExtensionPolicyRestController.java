package com.example.demo.controller;

import com.example.demo.controller.dto.req.ChangeFixedExtensionPolicyRequest;
import com.example.demo.controller.dto.req.CustomExtensionPolicyRequest;
import com.example.demo.controller.dto.res.CustomExtensionPolicyResponse;
import com.example.demo.controller.dto.res.ExtensionPolicyResponse;
import com.example.demo.controller.dto.res.FixedExtensionPolicyResponse;
import com.example.demo.domain.ExtensionPolicy;
import com.example.demo.service.ExtensionPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 확장자 정책 API 계약에 맞춰 정책 조회·변경·등록·삭제를 처리한다. */
@RestController
@RequestMapping("/api/v1/extension-policies")
@RequiredArgsConstructor
public class ExtensionPolicyRestController {

    private final ExtensionPolicyService service;

    /** DB의 최신 fixed·custom 정책을 계약된 응답 구조로 반환한다. */
    @GetMapping
    public ExtensionPolicyResponse getPolicies() {
        return ExtensionPolicyResponse.from(service.findAll());
    }

    /** 고정 확장자의 차단 상태를 변경하고 변경된 정책을 반환한다. */
    @PatchMapping("/fixed/{extension}")
    public FixedExtensionPolicyResponse changeFixedPolicy(
            @PathVariable String extension,
            @Valid @RequestBody ChangeFixedExtensionPolicyRequest request
    ) {
        ExtensionPolicy policy = service.changeFixedBlocked(extension, request.blocked());
        return FixedExtensionPolicyResponse.from(policy);
    }

    /** 커스텀 확장자를 등록하고 정규화된 결과를 201 응답으로 반환한다. */
    @PostMapping("/custom")
    public ResponseEntity<CustomExtensionPolicyResponse> registerCustomPolicy(
            @Valid @RequestBody CustomExtensionPolicyRequest request
    ) {
        ExtensionPolicy policy = service.registerCustom(request.extension());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomExtensionPolicyResponse.from(policy));
    }

    /** 커스텀 확장자를 물리 삭제하고 본문 없는 204 응답을 반환한다. */
    @DeleteMapping("/custom/{extension}")
    public ResponseEntity<Void> deleteCustomPolicy(@PathVariable String extension) {
        service.deleteCustom(extension);
        return ResponseEntity.noContent().build();
    }
}
