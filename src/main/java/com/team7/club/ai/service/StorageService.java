package com.team7.club.ai.service;




import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;


	public String uploadImage(byte[] imageBytes) {
		try {
			// Prepare the input stream for the image bytes
			InputStream inputStream = new ByteArrayInputStream(imageBytes);

			// Set the metadata for the S3 object
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("image/png"); // Assuming the image is a PNG

			// Generate a unique object key for the image
			String objectKey = UUID.randomUUID() + ".png";

			// Prepare the PutObjectRequest
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, inputStream, metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead);  // Make the image public

			// Upload the image to S3
			amazonS3Client.putObject(putObjectRequest);

			// Close the input stream
			inputStream.close();

			// Return the S3 URL of the uploaded image
			return amazonS3Client.getUrl(bucketName, objectKey).toString();
		} catch (Exception e) {
			throw new RuntimeException("Error uploading image to S3", e);
		}
	}

	// Method to upload the image to S3 and return the image URL
	public Mono<String> upload(String receivedUrl) {
		return Mono.fromCallable(() -> {
			InputStream inputStream = null;
			try {
				// Open the URL stream to get the image
				URL url = new URL(receivedUrl);
				inputStream = url.openStream();

				// Set metadata for the object, including content type
				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentType("image/png");  // You can enhance this by setting dynamic content type

				// Generate a unique key for the object in S3
				String objectKey = UUID.randomUUID() + ".png"; // Unique object key for S3
				PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, inputStream, metadata)
					.withCannedAcl(CannedAccessControlList.PublicRead);  // Make it public for access

				// Upload the image to S3
				amazonS3Client.putObject(putObjectRequest);

				// Close the input stream
				inputStream.close();

				// Return the URL of the uploaded image on S3
				return amazonS3Client.getUrl(bucketName, objectKey).toString();
			} catch (IOException e) {
				throw new RuntimeException("Error uploading to S3", e);
			} catch (AmazonServiceException e) {
				throw new RuntimeException("S3 Service error", e);
			} finally {
				// Ensure the input stream is closed to avoid resource leak
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();  // Handle closing error
					}
				}
			}
		});
	}

	// Method to download a file from S3
	public InputStream download(String fileName) {
		try {
			S3Object object = amazonS3Client.getObject(bucketName, fileName);
			return object.getObjectContent();
		} catch (AmazonServiceException e) {
			throw new IllegalStateException("Failed to download the file", e);
		}
	}

	// List all objects in the bucket
	public List<S3ObjectSummary> listObjects() {
		ObjectListing objectListing = amazonS3Client.listObjects(bucketName);
		return objectListing.getObjectSummaries();
	}

	// Delete an object from the S3 bucket
	public void deleteObject(String name) {
		amazonS3Client.deleteObject(bucketName, name);
	}
}