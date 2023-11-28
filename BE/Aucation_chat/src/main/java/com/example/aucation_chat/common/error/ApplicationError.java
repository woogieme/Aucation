package com.example.aucation_chat.common.error;

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
    FORBIDDEN_PARTICIPANT(HttpStatus.FORBIDDEN, "C002", "당신은 해당 채팅의 참여자가 아닙니다."),

    FILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "F001", "파일이 존재하지 않습니다."),

    AWS_S3_SAVE_ERROR(HttpStatus.BAD_REQUEST, "A001", "S3 파일 업로드를 실패했습니다."),
    AWS_S3_DELETE_ERROR(HttpStatus.BAD_REQUEST, "A002", "S3 파일 삭제를 실패했습니다."),
    AWS_REKOGNITION_ERROR(HttpStatus.BAD_REQUEST, "A003", "REKOGNITION 에러가 발생했습니다."),
    AUCTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "A004", "없는 경매장입니다"),
    AUCTION_HISTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "A005", "경매제품의 거래 내역이 존재하지 않습니다."),

    PRODUCT_CONFIRMED(HttpStatus.BAD_REQUEST, "P001", "거래가 끝난 물품입니다."),
    INVALID_PRODUCT(HttpStatus.BAD_REQUEST, "P002", "유효하지 않은 판매 유형입니다"),

    DISCOUNT_NOT_FOUND(HttpStatus.BAD_REQUEST, "D001", "없는 할인제품입니다."),
    DISCOUNT_HISTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "D002", "할인판매 거래 내역이 존재하지 않습니다."),

    INVALID_REDIS_KEY(HttpStatus.BAD_REQUEST, "R001", "유효하지 않은 Redis key입니다."),


    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 에러가 발생했습니다."),
    DUPLICATE_MEMBER_EMAIL(HttpStatus.BAD_REQUEST,"U015","이메일이 중복했습니다"),
    EXIST_EMAIL(HttpStatus.BAD_REQUEST, "U016","이미 가입한 이메일이 존재합니다." ),
    MEMBER_NOT_HAVE_MONEY(HttpStatus.BAD_REQUEST,"W001" ,"현재 돈이 너무나도 부족합니다. 충전을 부탁드립니다." ),
    OWNER_NOT_BID(HttpStatus.BAD_REQUEST,"B001" ,"당신은 판매자입니다 입찰할수 없습니다"),
    DUPLICATE_NOT_BID(HttpStatus.BAD_REQUEST, "B002","당신은 현재 최고 입찰자입니다 또 입찰할수없습니다." );
    private final HttpStatus status;
    private final String code;
    private final String message;
}
