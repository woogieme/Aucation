package com.example.aucation.reauction.api.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.photo.db.Photo;
import com.example.aucation.reauction.db.entity.ReAucBidPhoto;
import com.example.aucation.reauction.db.entity.ReAuctionBid;
import com.example.aucation.reauction.db.repository.ReAucBidPhotoRepository;
import com.example.aucation.reauction.db.repository.ReAuctionBidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReAucBidPhotoService {

	private final ReAuctionBidRepository reAuctionBidRepository;

	private final AmazonS3Client amazonS3Client;

	private final ReAucBidPhotoRepository reAucBidPhotoRepository;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	final String dirName = "bid";

	@Transactional
	public void upload(List<MultipartFile> files, Long reAucBidPk) throws IOException {
		ReAuctionBid reAuctionBid = reAuctionBidRepository.findById(reAucBidPk)
				.orElseThrow(()->new IOException("역경매 사진 에러에러 발생"));

		for(MultipartFile multipartFile: files) {
			File uploadFile = convertToFile(multipartFile)
				.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 변환에 실패했습니다."));

			String fileName = dirName + "/" + reAucBidPk + "/" +" " + uploadFile.getName();

			String uploadImageUrl = putS3(uploadFile, fileName);

			removeFile(uploadFile);

			ReAucBidPhoto reAucBidPhoto = ReAucBidPhoto.builder()
					.imgUrl(uploadImageUrl)
					.reAuctionBid(reAuctionBid)
					.build();
			reAucBidPhotoRepository.save(reAucBidPhoto);
		}


	}
	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
			.withCannedAcl(CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}
	private Optional<File> convertToFile(MultipartFile file) throws IOException, FileNotFoundException {
		File uploadFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
		FileOutputStream fos = new FileOutputStream(uploadFile);
		fos.write(file.getBytes());
		fos.close();
		return Optional.of(uploadFile);
	}

	private void removeFile(File targetFile) {
		if (targetFile.exists()) {
			if (targetFile.delete()) {
				log.info("파일이 삭제되었습니다.");
			} else {
				log.info("파일이 삭제되지 않았습니다.");
			}
		}
	}

	public List<String> getPhoto(ReAuctionBid reAucBid) {
		List<ReAucBidPhoto> list = reAucBidPhotoRepository.findByReAuctionBid(reAucBid);
		List<String> result = new ArrayList<>();
		if(list != null){
			for(ReAucBidPhoto bid : list){
				result.add(bid.getImgUrl());
			}
		}
		return result;
	}
}

