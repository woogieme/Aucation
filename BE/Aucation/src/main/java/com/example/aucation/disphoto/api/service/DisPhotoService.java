package com.example.aucation.disphoto.api.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.BadRequestException;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.discount.db.entity.Discount;
import com.example.aucation.discount.db.repository.DiscountRepository;
import com.example.aucation.disphoto.db.entity.DisPhoto;
import com.example.aucation.disphoto.db.repository.DisPhotoRepository;
import com.example.aucation.photo.api.service.PhotoService;
import com.example.aucation.photo.db.Photo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisPhotoService {

	private final DiscountRepository discountRepository;

	private final DisPhotoRepository disPhotoRepository;

	private final PhotoService photoService;

	final String dirName = "discount";

	@Transactional
	public void uploadDiscount(List<MultipartFile> files, String discountUUID) throws IOException {
		Discount discount = discountRepository.findByDiscountUUID(discountUUID).orElseThrow(()->new BadRequestException(
			ApplicationError.AWS_S3_SAVE_ERROR));

		for(MultipartFile multipartFile: files) {
			File uploadFile = photoService.convertToFile(multipartFile)
				.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 변환에 실패했습니다."));

			String fileName = dirName + "/" + discountUUID + "/" +" " + uploadFile.getName();

			String uploadImageUrl = photoService.putS3(uploadFile, fileName);

			photoService.removeFile(uploadFile);

			DisPhoto profileImg = DisPhoto.builder()
				.imgUrl(uploadImageUrl)
				.discount(discount)
				.build();
			disPhotoRepository.save(profileImg);
		}

	}

	public List<DisPhoto> getPhoto(Long discountId) {
		return disPhotoRepository.findByDiscount_Id(discountId)
			.orElseThrow(()-> new NotFoundException(ApplicationError.AWS_S3_SAVE_ERROR));
	}
}
