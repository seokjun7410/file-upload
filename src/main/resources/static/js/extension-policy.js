"use strict";

const POLICY_API_URL = "/api/v1/extension-policies";
const DEFAULT_LOAD_ERROR_MESSAGE = "확장자 정책을 불러오지 못했습니다.";
const DEFAULT_CHANGE_ERROR_MESSAGE = "고정 확장자 정책을 변경하지 못했습니다.";
const RESYNCHRONIZE_ERROR_MESSAGE =
    "최신 정책 상태도 확인하지 못했습니다. 페이지를 새로고침해 주세요.";

document.addEventListener("DOMContentLoaded", loadExtensionPolicies);

/**
 * 서버의 최신 확장자 정책을 조회해 화면에 표시한다.
 * 조회 실패 시 목록을 비우고 사용자가 이해할 수 있는 오류 메시지를 남긴다.
 */
async function loadExtensionPolicies() {
    showPolicyStatus("확장자 정책을 불러오는 중입니다.");

    try {
        const policies = await fetchExtensionPolicies();
        renderExtensionPolicies(policies);
        showPolicyStatus("확장자 정책을 불러왔습니다.");
    } catch (error) {
        clearPolicyLists();
        showPolicyStatus(resolvePolicyErrorMessage(error, DEFAULT_LOAD_ERROR_MESSAGE));
    }
}

/**
 * 서버에서 고정·커스텀 확장자 정책의 최신 상태를 조회한다.
 *
 * @returns {Promise<{fixed: {extension: string, blocked: boolean}[], custom: string[]}>}
 * 조회 화면과 실패 복구가 함께 사용하는 최신 정책
 */
async function fetchExtensionPolicies() {
    const response = await axios.get(POLICY_API_URL);
    return response.data;
}

/**
 * 서버 정책 응답으로 고정·커스텀 목록 전체를 다시 표시한다.
 *
 * @param {{fixed: {extension: string, blocked: boolean}[], custom: string[]}} policies
 * 서버가 반환한 최신 정책
 */
function renderExtensionPolicies(policies) {
    renderFixedPolicies(policies.fixed);
    renderCustomPolicies(policies.custom);
}

/**
 * 고정 확장자와 서버에 저장된 차단 상태를 변경 가능한 체크박스로 표시한다.
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
        checkbox.addEventListener("change", () => {
            changeFixedPolicy(policy.extension, checkbox, !checkbox.checked);
        });

        label.append(checkbox, document.createTextNode(` ${policy.extension}`));
        item.append(label);
        fixedPolicyList.append(item);
    }
}

/**
 * 고정 확장자의 차단 상태를 저장하고 서버 응답을 체크박스와 상태 메시지에 반영한다.
 * 실패하면 전체 정책을 다시 조회하며, 재조회도 실패할 때는 클릭 전 상태를 복원한다.
 *
 * @param {string} extension 변경할 고정 확장자
 * @param {HTMLInputElement} checkbox 사용자가 변경한 체크박스
 * @param {boolean} previousBlocked 사용자가 클릭하기 전 차단 상태
 */
async function changeFixedPolicy(extension, checkbox, previousBlocked) {
    setFixedPolicyControlsDisabled(true);
    showPolicyStatus(`${extension} 확장자 차단 상태를 저장하는 중입니다.`);

    try {
        const encodedExtension = encodeURIComponent(extension);
        const response = await axios.patch(
            `${POLICY_API_URL}/fixed/${encodedExtension}`,
            {blocked: checkbox.checked}
        );
        checkbox.checked = response.data.blocked;
        showPolicyStatus(resolveChangeSuccessMessage(response.data));
        setFixedPolicyControlsDisabled(false);
    } catch (error) {
        const changeErrorMessage = resolvePolicyErrorMessage(
            error,
            DEFAULT_CHANGE_ERROR_MESSAGE
        );
        await resynchronizeAfterChangeFailure(
            checkbox,
            previousBlocked,
            changeErrorMessage
        );
    }
}

/**
 * PATCH 실패 뒤 전체 정책을 다시 조회해 화면을 서버 상태에 맞춘다.
 * 재조회도 실패하면 기존 DOM에서 클릭 전 상태를 복원하고 사용자의 새로고침을 요청한다.
 *
 * @param {HTMLInputElement} checkbox 사용자가 변경한 체크박스
 * @param {boolean} previousBlocked 사용자가 클릭하기 전 차단 상태
 * @param {string} changeErrorMessage PATCH 요청에서 확인한 사용자 오류 메시지
 */
async function resynchronizeAfterChangeFailure(
    checkbox,
    previousBlocked,
    changeErrorMessage
) {
    showPolicyStatus(`${changeErrorMessage} 최신 정책 상태를 다시 확인하는 중입니다.`);

    try {
        const policies = await fetchExtensionPolicies();
        renderExtensionPolicies(policies);
        showPolicyStatus(changeErrorMessage);
    } catch (error) {
        checkbox.checked = previousBlocked;
        setFixedPolicyControlsDisabled(false);
        showPolicyStatus(`${changeErrorMessage} ${RESYNCHRONIZE_ERROR_MESSAGE}`);
    }
}

/**
 * 정책 변경 중 모든 고정 확장자 체크박스의 입력 가능 여부를 함께 변경한다.
 *
 * @param {boolean} disabled 체크박스를 비활성화할지 여부
 */
function setFixedPolicyControlsDisabled(disabled) {
    const checkboxes = document.querySelectorAll(
        "#fixed-policy-list input[type='checkbox']"
    );

    for (const checkbox of checkboxes) {
        checkbox.disabled = disabled;
    }
}

/**
 * 서버가 확정한 고정 확장자 상태를 사용자가 이해할 수 있는 성공 메시지로 바꾼다.
 *
 * @param {{extension: string, blocked: boolean}} policy 변경된 고정 확장자 정책
 * @returns {string} 차단 또는 차단 해제 결과를 설명하는 메시지
 */
function resolveChangeSuccessMessage(policy) {
    if (policy.blocked) {
        return `${policy.extension} 확장자를 차단했습니다.`;
    }
    return `${policy.extension} 확장자 차단을 해제했습니다.`;
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
 * 정책 조회와 변경 상태를 사용자에게 표시한다.
 *
 * @param {string} message 표시할 상태 메시지
 */
function showPolicyStatus(message) {
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
 * 공통 오류 응답의 메시지를 반환하고, 메시지가 없으면 작업별 기본 안내 문구를 반환한다.
 *
 * @param {unknown} error Axios가 반환한 정책 요청 오류
 * @param {string} fallbackMessage 공통 오류 메시지가 없을 때 사용할 안내 문구
 * @returns {string} 사용자에게 표시할 오류 메시지
 */
function resolvePolicyErrorMessage(error, fallbackMessage) {
    const responseMessage = error?.response?.data?.message;
    if (typeof responseMessage === "string" && responseMessage.trim() !== "") {
        return responseMessage;
    }
    return fallbackMessage;
}
