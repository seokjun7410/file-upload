"use strict";

const POLICY_API_URL = "/api/v1/extension-policies";
const DEFAULT_LOAD_ERROR_MESSAGE = "확장자 정책을 불러오지 못했습니다.";
const DEFAULT_CHANGE_ERROR_MESSAGE = "고정 확장자 정책을 변경하지 못했습니다.";
const DEFAULT_CUSTOM_ADD_ERROR_MESSAGE = "커스텀 확장자를 추가하지 못했습니다.";
const DEFAULT_CUSTOM_DELETE_ERROR_MESSAGE = "커스텀 확장자를 삭제하지 못했습니다.";
const DEFAULT_FILE_UPLOAD_ERROR_MESSAGE = "파일을 업로드하지 못했습니다.";
const UPLOAD_MAX_RETRIES = 3;
const UPLOAD_MAX_DURATION_MS = 30 * 1000;
const UPLOAD_RESULT_CONFIRMATION_MESSAGE =
    "자동 재시도를 종료했습니다. 업로드 결과를 확인한 뒤 다시 시도해 주세요.";
const RESYNCHRONIZE_ERROR_MESSAGE =
    "최신 정책 상태도 확인하지 못했습니다. 페이지를 새로고침해 주세요.";

document.addEventListener("DOMContentLoaded", initializeExtensionPolicyPage);

/**
 * 정책 조회와 커스텀 확장자 추가·삭제 화면의 이벤트를 초기화한다.
 */
function initializeExtensionPolicyPage() {
    bindCustomPolicyForm();
    bindFileUploadForm();
    loadExtensionPolicies();
}

/**
 * 커스텀 확장자 폼 제출을 서버 등록 동작에 연결한다.
 */
function bindCustomPolicyForm() {
    const form = document.getElementById("custom-policy-form");
    form.addEventListener("submit", (event) => {
        event.preventDefault();
        registerCustomPolicy();
    });
}

/**
 * 파일 업로드 폼 제출을 서버 업로드 동작에 연결한다.
 */
function bindFileUploadForm() {
    const form = document.getElementById("file-upload-form");
    form.addEventListener("submit", (event) => {
        event.preventDefault();
        uploadFile();
    });
}

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
 * 커스텀 확장자 변경이 실패하거나 응답이 불확실할 때 정책 목록을 서버 상태로 맞춘다.
 * 재조회도 실패하면 현재 목록을 유지하고 사용자가 새로고침하도록 안내한다.
 *
 * @param {string} policyErrorMessage 커스텀 확장자 변경에서 확인한 사용자 오류 메시지
 */
async function resynchronizeAfterCustomPolicyFailure(policyErrorMessage) {
    showPolicyStatus(`${policyErrorMessage} 최신 정책 상태를 다시 확인하는 중입니다.`);

    try {
        const policies = await fetchExtensionPolicies();
        renderExtensionPolicies(policies);
        showPolicyStatus(policyErrorMessage);
    } catch (error) {
        showPolicyStatus(`${policyErrorMessage} ${RESYNCHRONIZE_ERROR_MESSAGE}`);
    }
}

/**
 * 커스텀 확장자를 등록하고 성공한 경우 서버의 최신 정책 목록을 다시 표시한다.
 *
 * @returns {Promise<void>} 등록 요청과 최신 목록 반영이 끝난 뒤 완료된다.
 */
async function registerCustomPolicy() {
    const input = document.getElementById("custom-extension-input");
    setCustomPolicyControlsDisabled(true);
    showPolicyStatus("커스텀 확장자를 추가하는 중입니다.");

    try {
        const response = await axios.post(`${POLICY_API_URL}/custom`, {
            extension: input.value
        });

        input.value = "";
        try {
            const policies = await fetchExtensionPolicies();
            renderExtensionPolicies(policies);
            showPolicyStatus(`${response.data.extension} 확장자를 추가했습니다.`);
        } catch (error) {
            showPolicyStatus(
                `${response.data.extension} 확장자 추가는 완료되었지만 ${RESYNCHRONIZE_ERROR_MESSAGE}`
            );
        }
    } catch (error) {
        const changeErrorMessage = resolvePolicyErrorMessage(
            error,
            DEFAULT_CUSTOM_ADD_ERROR_MESSAGE
        );
        await resynchronizeAfterCustomPolicyFailure(changeErrorMessage);
    } finally {
        setCustomPolicyControlsDisabled(false);
    }
}

/**
 * 커스텀 확장자를 삭제하고 서버의 최신 정책 목록을 다시 표시한다.
 * 삭제 또는 후속 조회가 실패하면 공통 오류 메시지와 재동기화 결과를 표시한다.
 *
 * @param {string} extension 삭제할 커스텀 확장자
 * @returns {Promise<void>} 삭제 요청과 최신 목록 반영이 끝난 뒤 완료된다.
 */
async function deleteCustomPolicy(extension) {
    setCustomPolicyControlsDisabled(true);
    showPolicyStatus(`${extension} 확장자를 삭제하는 중입니다.`);

    try {
        const encodedExtension = encodeURIComponent(extension);
        await axios.delete(`${POLICY_API_URL}/custom/${encodedExtension}`);

        try {
            const policies = await fetchExtensionPolicies();
            renderExtensionPolicies(policies);
            showPolicyStatus(`${extension} 확장자를 삭제했습니다.`);
        } catch (error) {
            showPolicyStatus(
                `${extension} 확장자 삭제는 완료되었지만 ${RESYNCHRONIZE_ERROR_MESSAGE}`
            );
        }
    } catch (error) {
        const policyErrorMessage = resolvePolicyErrorMessage(
            error,
            DEFAULT_CUSTOM_DELETE_ERROR_MESSAGE
        );
        await resynchronizeAfterCustomPolicyFailure(policyErrorMessage);
    } finally {
        setCustomPolicyControlsDisabled(false);
    }
}

/**
 * 선택된 파일을 FormData로 서버에 업로드하고 서버가 생성한 파일명을 표시한다.
 * 확장자 차단 여부는 화면에서 판단하지 않고 서버 응답을 최종 결과로 사용한다.
 * 한 번의 논리 업로드 동안 같은 UUID v4를 Idempotency-Key로 유지한다.
 *
 * @returns {Promise<void>} 업로드 요청과 화면 결과 표시가 끝난 뒤 완료된다.
 */
async function uploadFile() {
    const input = document.getElementById("file-input");
    const formData = new FormData();
    const file = input.files[0];
    if (file) {
        formData.append("file", file);
    }

    setFileUploadControlsDisabled(true);
    showFileUploadStatus("파일을 업로드하는 중입니다.");

    const requestId = crypto.randomUUID();
    const startedAt = Date.now();
    let retryCount = 0;

    try {
        while (true) {
            try {
                const response = await axios.post("/api/v1/files", formData, {
                    headers: {"Idempotency-Key": requestId}
                });
                input.value = "";
                showFileUploadStatus(
                    `${response.data.message} 저장된 파일명: ${response.data.filename}`
                );
                break;
            } catch (error) {
                const retryAfterSeconds = resolveRetryAfterSeconds(error);
                const elapsedMs = Date.now() - startedAt;
                const waitMs = retryAfterSeconds * 1000;
                const canRetry = error?.response?.status === 409
                    && error?.response?.data?.code === "IDEMPOTENCY_IN_PROGRESS"
                    && retryCount < UPLOAD_MAX_RETRIES
                    && elapsedMs + waitMs <= UPLOAD_MAX_DURATION_MS;

                if (!canRetry) {
                    if (error?.response?.status === 409
                        && error?.response?.data?.code === "IDEMPOTENCY_IN_PROGRESS") {
                        showFileUploadStatus(UPLOAD_RESULT_CONFIRMATION_MESSAGE);
                    } else {
                        showFileUploadStatus(
                            resolvePolicyErrorMessage(error, DEFAULT_FILE_UPLOAD_ERROR_MESSAGE)
                        );
                    }
                    break;
                }

                retryCount++;
                showFileUploadStatus(
                    `업로드 처리 중입니다. ${retryAfterSeconds}초 후 재시도합니다. (${retryCount}/${UPLOAD_MAX_RETRIES})`
                );
                await delay(retryAfterSeconds * 1000);
            }
        }
    } catch (error) {
        showFileUploadStatus(
            resolvePolicyErrorMessage(error, DEFAULT_FILE_UPLOAD_ERROR_MESSAGE)
        );
    } finally {
        setFileUploadControlsDisabled(false);
    }
}

/** 서버가 계산한 Retry-After 초 값을 안전한 범위로 해석한다. */
function resolveRetryAfterSeconds(error) {
    const value = Number.parseInt(error?.response?.headers?.["retry-after"], 10);
    if (!Number.isFinite(value)) {
        return 1;
    }
    return Math.min(30, Math.max(1, value));
}

/** 재시도 전에 서버가 지정한 대기 시간을 기다린다. */
function delay(milliseconds) {
    return new Promise((resolve) => setTimeout(resolve, milliseconds));
}

/**
 * 커스텀 확장자 변경 중 입력창·추가 버튼·삭제 버튼의 입력 가능 여부를 함께 변경한다.
 *
 * @param {boolean} disabled 입력 요소를 비활성화할지 여부
 */
function setCustomPolicyControlsDisabled(disabled) {
    document.getElementById("custom-extension-input").disabled = disabled;
    document.getElementById("custom-extension-add-button").disabled = disabled;

    const deleteButtons = document.querySelectorAll(
        "#custom-policy-list button[data-custom-policy-delete]"
    );
    for (const deleteButton of deleteButtons) {
        deleteButton.disabled = disabled;
    }
}

/**
 * 파일 업로드 중 파일 선택과 업로드 버튼의 입력 가능 여부를 함께 변경한다.
 *
 * @param {boolean} disabled 업로드 요소를 비활성화할지 여부
 */
function setFileUploadControlsDisabled(disabled) {
    document.getElementById("file-input").disabled = disabled;
    document.getElementById("file-upload-button").disabled = disabled;
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
 * 커스텀 확장자를 삭제 버튼과 함께 목록으로 표시하고, 항목이 없으면 빈 상태를 안내한다.
 *
 * @param {string[]} extensions 커스텀 확장자 목록
 */
function renderCustomPolicies(extensions) {
    const customPolicyList = document.getElementById("custom-policy-list");
    customPolicyList.replaceChildren();

    if (extensions.length === 0) {
        appendCustomPolicyEmptyItem(customPolicyList);
        return;
    }

    for (const extension of extensions) {
        appendCustomPolicyItem(customPolicyList, extension);
    }
}

/**
 * 커스텀 목록이 비어 있을 때 빈 상태 안내를 추가한다.
 *
 * @param {HTMLElement} customPolicyList 커스텀 확장자 목록 요소
 */
function appendCustomPolicyEmptyItem(customPolicyList) {
    const item = document.createElement("li");
    item.textContent = "등록된 커스텀 확장자가 없습니다.";
    customPolicyList.append(item);
}

/**
 * 커스텀 목록에 확장자와 삭제 버튼을 안전하게 추가한다.
 *
 * @param {HTMLElement} customPolicyList 항목을 추가할 목록 요소
 * @param {string} extension 화면에 표시하고 삭제할 확장자
 */
function appendCustomPolicyItem(customPolicyList, extension) {
    const item = document.createElement("li");
    const extensionText = document.createElement("span");
    extensionText.textContent = extension;
    const deleteButton = document.createElement("button");
    deleteButton.type = "button";
    deleteButton.textContent = "X";
    deleteButton.dataset.customPolicyDelete = "true";
    deleteButton.setAttribute("aria-label", `${extension} 삭제`);
    deleteButton.addEventListener("click", () => deleteCustomPolicy(extension));

    item.append(extensionText, deleteButton);
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
 * 파일 업로드 진행·성공·실패 상태를 사용자에게 표시한다.
 *
 * @param {string} message 표시할 업로드 상태 메시지
 */
function showFileUploadStatus(message) {
    document.getElementById("file-upload-status").textContent = message;
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
