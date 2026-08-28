"use strict";

const POLICY_API_URL = "/api/v1/extension-policies";
const DEFAULT_LOAD_ERROR_MESSAGE = "확장자 정책을 불러오지 못했습니다.";

document.addEventListener("DOMContentLoaded", loadExtensionPolicies);

/**
 * 서버의 최신 확장자 정책을 조회해 화면에 표시한다.
 * 조회 실패 시 목록을 비우고 사용자가 이해할 수 있는 오류 메시지를 남긴다.
 */
async function loadExtensionPolicies() {
    showLoadStatus("확장자 정책을 불러오는 중입니다.");

    try {
        const response = await axios.get(POLICY_API_URL);
        renderFixedPolicies(response.data.fixed);
        renderCustomPolicies(response.data.custom);
        showLoadStatus("확장자 정책을 불러왔습니다.");
    } catch (error) {
        clearPolicyLists();
        showLoadStatus(resolveLoadErrorMessage(error));
    }
}

/**
 * 고정 확장자와 서버에 저장된 차단 상태를 읽기 전용 체크박스로 표시한다.
 *
 * @param {{extension: string, blocked: boolean}[]} policies 고정 확장자 정책 목록
 */
function renderFixedPolicies(policies) {
    const fixedPolicyList = document.getElementById("fixed-policy-list");
    fixedPolicyList.replaceChildren();

    for (const policy of policies) {
        const item = document.createElement("li");
        const label = document.createElement("label");
        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.checked = policy.blocked;
        checkbox.disabled = true;

        label.append(checkbox, document.createTextNode(` ${policy.extension}`));
        item.append(label);
        fixedPolicyList.append(item);
    }
}

/**
 * 커스텀 확장자를 목록으로 표시하고, 항목이 없으면 빈 상태를 안내한다.
 *
 * @param {string[]} extensions 커스텀 확장자 목록
 */
function renderCustomPolicies(extensions) {
    const customPolicyList = document.getElementById("custom-policy-list");
    customPolicyList.replaceChildren();

    if (extensions.length === 0) {
        appendCustomPolicyItem(customPolicyList, "등록된 커스텀 확장자가 없습니다.");
        return;
    }

    for (const extension of extensions) {
        appendCustomPolicyItem(customPolicyList, extension);
    }
}

/**
 * 커스텀 목록에 문자열 항목을 안전하게 추가한다.
 *
 * @param {HTMLElement} customPolicyList 항목을 추가할 목록 요소
 * @param {string} text 화면에 표시할 확장자 또는 빈 상태 문구
 */
function appendCustomPolicyItem(customPolicyList, text) {
    const item = document.createElement("li");
    item.textContent = text;
    customPolicyList.append(item);
}

/**
 * 정책 조회 상태를 사용자에게 표시한다.
 *
 * @param {string} message 표시할 상태 메시지
 */
function showLoadStatus(message) {
    document.getElementById("policy-load-status").textContent = message;
}

/**
 * 정책 조회가 실패했을 때 기존 화면 목록을 제거한다.
 */
function clearPolicyLists() {
    document.getElementById("fixed-policy-list").replaceChildren();
    document.getElementById("custom-policy-list").replaceChildren();
}

/**
 * 공통 오류 응답의 메시지를 반환하고, 메시지가 없으면 기본 안내 문구를 반환한다.
 *
 * @param {unknown} error Axios가 반환한 조회 오류
 * @returns {string} 사용자에게 표시할 오류 메시지
 */
function resolveLoadErrorMessage(error) {
    const responseMessage = error?.response?.data?.message;
    if (typeof responseMessage === "string" && responseMessage.trim() !== "") {
        return responseMessage;
    }
    return DEFAULT_LOAD_ERROR_MESSAGE;
}
