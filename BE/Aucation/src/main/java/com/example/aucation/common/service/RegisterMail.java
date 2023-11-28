package com.example.aucation.common.service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.aucation.member.db.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegisterMail {
	private final JavaMailSender mailSender;
	private final String adminEmail = "wodnr8462@naver.com";
	private final MemberRepository memberRepository;

	private String code;

	public MimeMessage createMessage(String userEmail) throws MessagingException, UnsupportedEncodingException {

		MimeMessage message = mailSender.createMimeMessage();

		message.addRecipients(Message.RecipientType.TO, userEmail);// 보내는 대상
		message.setSubject("Aucation 회원가입 이메일 인증");// 제목

		String msgg = "";
		msgg += "<div style='margin:100px;'>";
		msgg += "<h1> 안녕하세요</h1>";
		msgg += "<h1> 지역 경매 사이트 Aucation 입니다</h1>";
		msgg += "<br>";
		msgg += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요.<p>";
		msgg += "<br>";
		msgg += "<p>저희 서비스를 이용해 주셔서 감사합니다.<p>";
		msgg += "<br>";
		msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
		msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
		msgg += "<div style='font-size:130%'>";
		msgg += "CODE : <strong>";
		msgg += code + "</strong><div><br/> "; // 메일에 인증번호 넣기
		msgg += "</div>";
		message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
		// 보내는 사람의 이메일 주소, 보내는 사람 이름
		message.setFrom(new InternetAddress(adminEmail, "Aucation_Admin"));// 보내는 사람

		return message;
	}

	public MimeMessage searchPwMessage(String userEmail) throws MessagingException, UnsupportedEncodingException {

		MimeMessage message = mailSender.createMimeMessage();

		message.addRecipients(Message.RecipientType.TO, userEmail);// 보내는 대상
		message.setSubject("Aucation 임시 비밀번호 전송");// 제목

		String msgg = "";
		msgg += "<div style='margin:100px;'>";
		msgg += "<h1> 안녕하세요</h1>";
		msgg += "<h1> 지역 경매 사이트 Aucation 입니다</h1>";
		msgg += "<br>";
		msgg += "<p>아래 임시 비밀번호로 로그인 후 비밀번호를 꼭 변경해주세요.<p>";
		msgg += "<br>";
		msgg += "<p>저희 서비스를 이용해 주셔서 감사합니다.<p>";
		msgg += "<br>";
		msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
		msgg += "<h3 style='color:blue;'>임시 비밀번호입니다.</h3>";
		msgg += "<div style='font-size:130%'>";
		msgg += "CODE : <strong>";
		msgg += code + "</strong><div><br/> "; // 메일에 인증번호 넣기
		msgg += "</div>";
		message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
		// 보내는 사람의 이메일 주소, 보내는 사람 이름
		message.setFrom(new InternetAddress(adminEmail, "Aucation_Admin"));// 보내는 사람

		return message;
	}

	public String createKey() {
		StringBuffer key = new StringBuffer();
		Random random = new Random();

		for (int i = 0; i < 8; i++) { // 인증코드 8자리
			int index = random.nextInt(3); // 0~2 까지 랜덤, rnd 값에 따라서 아래 switch 문이 실행됨

			switch (index) {
				case 0:
					key.append((char)((int)(random.nextInt(26)) + 97));
					// a~z (ex. 1+97=98 => (char)98 = 'b')
					break;
				case 1:
					key.append((char)((int)(random.nextInt(26)) + 65));
					// A~Z
					break;
				case 2:
					key.append((random.nextInt(10)));
					// 0~9
					break;
			}
		}

		return key.toString();
	}

	// 메일 발송
	public String sendSimpleMessage(String userEmail, String type) throws Exception {
		MimeMessage message;

		if(type.equals("회원가입")) {
			memberRepository.findByMemberEmail(userEmail).ifPresent(user -> {
				throw new RuntimeException("유저가 이미 존재합니다.");
			});
			code = createKey(); // 랜덤 인증번호 생성
			message = createMessage(userEmail); // 메일 발송
		} else {
			code = type;
			message = searchPwMessage(userEmail); // 메일 발송
		}
		try {
			mailSender.send(message);
		} catch (MailException e) {
			throw new IllegalArgumentException("유효하지 않은 이메일입니다.");
		}

		return code; // 메일로 보냈던 인증 코드를 서버로 반환
	}

}
