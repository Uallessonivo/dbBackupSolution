package com.project.dbbackupsolution.domain.services;

import java.io.File;
import java.util.List;

public interface FileService {
    List<File> extractFiles(String path, String fileExtension);
}
