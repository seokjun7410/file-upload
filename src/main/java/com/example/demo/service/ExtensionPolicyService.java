package com.example.demo.service;

import com.example.demo.domain.ExtensionPolicy;
import java.util.List;

/** 확장자 정책의 조회·변경·등록·삭제 기능을 호출자에게 제공하는 서비스 인터페이스다. */
public interface ExtensionPolicyService {

    /** 저장된 fixed·custom 정책을 고정 카탈로그와 확장자 순서로 반환한다. */
    List<ExtensionPolicy> findAll();

    /** 커스텀 정책을 정규화해 저장하고, 중복·최대 개수 위반 시 등록을 거부한다. */
    ExtensionPolicy registerCustom(String extension);

    /** 고정 정책의 차단 상태를 변경하고 저장된 최신 정책을 반환한다. */
    ExtensionPolicy changeFixedBlocked(String extension, boolean blocked);

    /** 커스텀 정책을 정규화된 확장자 기준으로 물리 삭제한다. */
    void deleteCustom(String extension);
}
