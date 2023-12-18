package com.project.dbbackupsolution.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileRequest {
    private String path;
    private String fileExtension;
}
