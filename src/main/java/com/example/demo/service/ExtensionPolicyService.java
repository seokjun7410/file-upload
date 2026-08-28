package com.example.demo.service;

import com.example.demo.domain.ExtensionPolicy;

/** 커스텀 확장자 정책 등록 기능을 호출자에게 제공하는 서비스 인터페이스다. */
public interface ExtensionPolicyService {

    /** 커스텀 정책을 정규화해 저장하고, 중복·최대 개수 위반 시 등록을 거부한다. */
    ExtensionPolicy registerCustom(String extension);
}
