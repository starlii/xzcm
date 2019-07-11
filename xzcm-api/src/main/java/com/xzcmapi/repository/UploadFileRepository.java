package com.xzcmapi.repository;

import com.xzcmapi.entity.UploadFile;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UploadFileRepository extends MongoRepository<UploadFile, String> {
}
