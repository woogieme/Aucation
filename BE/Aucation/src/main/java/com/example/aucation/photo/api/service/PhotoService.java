package com.example.aucation.photo.api.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;

import com.example.aucation.discount.db.entity.Discount;
import com.example.aucation.discount.db.repository.DiscountRepository;
import com.example.aucation.photo.db.PhotoStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.auction.db.repository.AuctionRepository;
import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.BadRequestException;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.photo.db.Photo;
import com.example.aucation.photo.db.repository.PhotoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

	private final PhotoRepository photoRepository;

	private final AmazonS3Client amazonS3Client;

	private final AuctionRepository auctionRepository;

	@Qualifier("threadExecutor")
	@Autowired
	private Executor executor;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	final String dirName = "auction";


	public void upload(List<MultipartFile> files, String auctionUUID) {
		Auction auction = auctionRepository.findByAuctionUUID(auctionUUID).orElseThrow(()->new BadRequestException(
			ApplicationError.AWS_S3_SAVE_ERROR));

		files.forEach(file -> {
			try {
				this.uploadS3Object(auction,file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

	}

	protected void uploadS3Object(Auction auction, MultipartFile files) throws IOException {

		Runnable runnable = () -> {
			log.info("Thread Name : {}", Thread.currentThread().getName());
			File uploadFile = null;
			try {
				uploadFile = convertToFile(files)
					.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 변환에 실패했습니다."));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			String fileName = dirName + "/" + auction.getAuctionUUID() + "/" + " " + uploadFile.getName();

			String uploadImageUrl = putS3(uploadFile, fileName);

			removeFile(uploadFile);

			Photo profileImg = Photo.builder()
				.imgUrl(uploadImageUrl)
				.auction(auction)
				.build();
			photoRepository.save(profileImg);
		};
		executor.execute(runnable);
	}

	public String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
			.withCannedAcl(CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}
	public Optional<File> convertToFile(MultipartFile file) throws IOException, FileNotFoundException {
		File uploadFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
		FileOutputStream fos = new FileOutputStream(uploadFile);
		fos.write(file.getBytes());
		fos.close();
		return Optional.of(uploadFile);
	}

	public void removeFile(File targetFile) {
		if (targetFile.exists()) {
			if (targetFile.delete()) {
				log.info("파일이 삭제되었습니다.");
			} else {
				log.info("파일이 삭제되지 않았습니다.");
			}
		}
	}

	public List<Photo> getPhoto(long auctionPk) {
		return photoRepository.findByAuctionId(auctionPk)
				.orElseThrow(()-> new NotFoundException(ApplicationError.AWS_S3_SAVE_ERROR));
	}
	public Photo getOnePhoto(long auctionPk) {
		return photoRepository.findFirstByAuctionIdOrderByIdAsc(auctionPk)
				.orElseThrow(()-> new NotFoundException(ApplicationError.AWS_S3_SAVE_ERROR));
	}

}

