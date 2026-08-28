package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** 파일 업로드 화면을 정적 Thymeleaf 페이지로 제공한다. */
@Controller
public class FileUploadPageController {

    /** 서버 모델 없이 파일 업로드 화면을 반환한다. */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
