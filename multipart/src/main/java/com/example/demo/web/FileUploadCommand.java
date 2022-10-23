package com.example.demo.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@Data
@Builder()
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class FileUploadCommand {
    private String name;
    private FilePart file;
}
