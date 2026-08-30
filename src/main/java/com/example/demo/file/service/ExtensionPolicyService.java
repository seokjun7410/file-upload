package com.example.demo.file.service;

import com.example.demo.file.domain.entity.ExtensionPolicy;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import java.util.List;

/** 확장자 정책의 조회·변경·등록·삭제·차단 판정 기능을 호출자에게 제공하는 서비스 인터페이스다. */
public interface ExtensionPolicyService {

    /** fixed와 custom 정책을 각각 확장자명 오름차순으로 조회해 fixed부터 반환한다. */
    List<ExtensionPolicy> findAll();

    /** 검증된 커스텀 확장자 이름을 저장하고, 중복·최대 개수 위반 시 등록을 거부한다. */
    ExtensionPolicy registerCustom(ExtensionName extension);

    /** 고정 정책의 차단 상태를 변경하고 저장된 최신 정책을 반환한다. */
    ExtensionPolicy changeFixedBlocked(ExtensionName extension, boolean blocked);

    /** 검증된 확장자 이름에 해당하는 커스텀 정책을 물리 삭제한다. */
    void deleteCustom(ExtensionName extension);

    /** 확장자 이름에 저장된 차단 정책이 적용되는지 반환한다. */
    boolean isBlocked(ExtensionName extension);
}
