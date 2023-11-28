package com.example.aucation.common.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationError {

    VERIFICATION_CODE_NOT_EQUAL(HttpStatus.BAD_REQUEST, "C001", "인증번호가 일치하지 않습니다."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "C002", "잘못된 양식입니다."),
    INVALID_AUTHORITY(HttpStatus.BAD_REQUEST, "C003", "잘못된 권한입니다."),
    WRONG_ACCESS(HttpStatus.BAD_REQUEST, "C004", "잘못된 접근입니다."),
    INVALID_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "C005", "Token 타입이 올바르지 않습니다."),
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "C006", "메일 전송 요청에 실패했습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "C007", "Access Token이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "C008", "Refresh Token이 유효하지 않습니다."),

    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "U001", "인증되지 않은 사용자입니다."),
    FORBIDDEN_MEMBER(HttpStatus.FORBIDDEN, "U002", "권한이 없는 사용자입니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "U003", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "U004", "해당 아이디가 이미 존재합니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "U005", "해당 닉네임이 이미 존재합니다."),
    INVALID_COMPANY(HttpStatus.BAD_REQUEST, "U006", "지원하지 않는 회사입니다."),
    NOT_EQUAL_ID_OR_PASSWORD(HttpStatus.BAD_REQUEST, "U007", "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_GENDER(HttpStatus.BAD_REQUEST, "U008", "지원하지 않는 성별입니다."),
    NOT_EXIST_REGISTERED_TEAM(HttpStatus.BAD_REQUEST, "U009", "등록된 팀이 존재하지 않습니다."),
    BAD_MEMBER(HttpStatus.BAD_REQUEST, "U010", "비정상 사용자입니다."),
    NOT_EXIST_REGISTERED_IMAGES(HttpStatus.BAD_REQUEST, "U010", "등록된 프로필 이미지가 존재하지 않습니다."),
    INVALID_SMOKE_STATUS(HttpStatus.BAD_REQUEST, "U011", "지원하지 않는 흡연 상태입니다."),
    INVALID_DRINK_STATUS(HttpStatus.BAD_REQUEST, "U012", "지원하지 않는 음주 상태입니다."),
    HOBBY_NOT_FOUND(HttpStatus.BAD_REQUEST, "U013", "취미 키워드를 찾을 수 없습니다."),
    PROFILE_IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "U014", "프로필 이미지를 찾을 수 없습니다."),
    PROFILE_IMAGE_NOT_FACE(HttpStatus.BAD_REQUEST, "U015", "프로필 이미지에 얼굴이 존재하지 않습니다."),

    TEAM_NOT_FOUND(HttpStatus.BAD_REQUEST, "T001", "팀을 찾을 수 없습니다."),
    GENDER_MISMATCH(HttpStatus.BAD_REQUEST, "T002", "해당 성별은 팀에 참여할 수 없습니다."),
    ALREADY_IN_TEAM(HttpStatus.BAD_REQUEST, "T003", "이미 팀에 가입되었습니다."),
    TEAM_ALREADY_FULL(HttpStatus.BAD_REQUEST, "T004", "이미 팀 모집이 완료되었습니다."),

    RECOMMEND_NOT_FOUND(HttpStatus.BAD_REQUEST, "R001", "추천을 찾을 수 없습니다."),

    LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "L001", "호감을 찾을 수 없습니다."),

    MEETING_NOT_FOUND(HttpStatus.BAD_REQUEST, "M001", "미팅을 찾을 수 없습니다."),

    INSTANT_MEETING_NOT_FOUND(HttpStatus.BAD_REQUEST, "I001", "번개팅을 찾을 수 없습니다."),
    INSTANT_MEETING_ALREADY_FULL(HttpStatus.BAD_REQUEST, "I002", "이미 번개팅 모집이 완료되었습니다."),

    CHATTING_ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "C001", "채팅방을 찾을 수 없습니다."),

    FILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "F001", "파일이 존재하지 않습니다."),

    AWS_S3_SAVE_ERROR(HttpStatus.BAD_REQUEST, "A001", "S3 파일 업로드를 실패했습니다."),
    AWS_S3_DELETE_ERROR(HttpStatus.BAD_REQUEST, "A002", "S3 파일 삭제를 실패했습니다."),
    AWS_REKOGNITION_ERROR(HttpStatus.BAD_REQUEST, "A003", "REKOGNITION 에러가 발생했습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 에러가 발생했습니다."),
    DUPLICATE_MEMBER_EMAIL(HttpStatus.BAD_REQUEST,"U015","이메일이 중복했습니다"),
    EXIST_EMAIL(HttpStatus.BAD_REQUEST, "U016","이미 가입한 이메일이 존재합니다." ),
    MEMBER_NOT_HAVE_MONEY(HttpStatus.BAD_REQUEST,"W001" ,"현재 돈이 너무나도 부족합니다. 충전을 부탁드립니다." ),
    OWNER_NOT_BID(HttpStatus.BAD_REQUEST,"B001" ,"당신은 판매자입니다 입찰할수 없습니다"),
    DUPLICATE_NOT_BID(HttpStatus.BAD_REQUEST, "B002","당신은 현재 최고 입찰자입니다 또 입찰할수없습니다." ),
    NOT_TIME_BID(HttpStatus.BAD_REQUEST,"B003" ,"입찰할 수 있는 시간이 아닙니다."),
    EXIST_IMPUID(HttpStatus.BAD_REQUEST,"P001","UID가 존재합니다"),
    DISCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND,"D001","존재하지 않은 할인상품입니다" ),
    NOT_VERIFY_SMALL_BUSINESS(HttpStatus.BAD_REQUEST,"V001" ,"소상공인이 아닙니다 먼저 사업자 등록을 해주세요"),
    ALREADY_EXISTS_PURCHASE_USER(HttpStatus.BAD_REQUEST,"D002" ,"이미 누군가가 구매했습니다. 다른 상품을 찾아주세요"),
    YOU_ARE_OWNER(HttpStatus.BAD_REQUEST,"D003","당신은 해당 소상공인입니다 자기의 상품은 구매할수없습니다."),
    CLOSE_THE_AUCTION(HttpStatus.BAD_REQUEST,"P002" ,"이미 닫힌 옥션입니다. 죄송합니다 나가주세요"),
    NOT_EXIST_AUCTION(HttpStatus.BAD_REQUEST,"P003" ,"존재하지 않은 옥션입니다."),
    EXIST_BID_HISTORY(HttpStatus.BAD_REQUEST,"P004" ,"경매를 선택한 내역이 있습니다."),
    NOT_EXIST_BID(HttpStatus.BAD_REQUEST,"P005" ,"입찰 내역이 존재하지 않습니다."),
    EXIST_OWN_BID(HttpStatus.BAD_REQUEST,"P006" ,"입찰했던 내역이 있습니다."),
    NOT_EXIST_HISTORY(HttpStatus.BAD_REQUEST,"P007" ,"구매 예정인 입찰 내역이 없습니다."),
    NOT_OWNER(HttpStatus.BAD_REQUEST,"P008" ,"역경매 등록자가 아닙니다. 등록자만 입찰을 선택할 수 있습니다."),
    DISCOUNT_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND,"D004","History가 존재하지않습니다" ),
    NOT_CHEAPER_PRODUCT_PRICE(HttpStatus.NOT_FOUND,"D005","할인가가 정가보다 비싸거나 똑같습니다 다시입력해주세요"),
    EARLY_START_AUCTION(HttpStatus.BAD_REQUEST,"P009" , "아직 경매 시작 시간이 아닙니다 경매시작 시간에 맞춰서 경매를 진행해주세요"),
    STARTED_AUCTION(HttpStatus.BAD_REQUEST,"P010","이미 시작된 경매입니다 삭제할수 없습니다"),
    NOT_SELL_REAUCTION(HttpStatus.BAD_REQUEST, "P011","낙찰하기 직전입니다 삭제할수 없습니다" ),
    NOT_SELL_DISCOUNT(HttpStatus.BAD_REQUEST,"D006" ,"낙찰하기 직전입니다 삭제할수 없습니다"),
    LESS_MORE_THAN_START_PRICE(HttpStatus.BAD_REQUEST,"M001","시작하는 항상 1000원 이상입니다" );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
