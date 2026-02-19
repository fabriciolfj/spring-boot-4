package com.github.fabriciolfj.study.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.github.fabriciolfj.study.constants.Constants.UPLOAD_DIR;

@RestController
public class UploadFileController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileStreaming(@RequestPart("filePart") MultipartFile filePart) throws IOException {
        Path targetPath = UPLOAD_DIR.resolve(Objects.requireNonNull(filePart.getOriginalFilename()));
        Files.createDirectories(targetPath.getParent());
        try (InputStream inputStream = filePart.getInputStream(); OutputStream outputStream = Files.newOutputStream(targetPath)) {
            inputStream.transferTo(outputStream);
        }
        return ResponseEntity.ok("Upload successful: " + filePart.getOriginalFilename());
    }

    @GetMapping("/download")
    public StreamingResponseBody downloadFiles(HttpServletResponse response) throws IOException {
        String boundary = "filesBoundary";
        response.setContentType("multipart/mixed; boundary=" + boundary);
        List<Path> files = List.of(UPLOAD_DIR.resolve("file1.txt"), UPLOAD_DIR.resolve("file2.txt"));
        return outputStream -> {
            try (BufferedOutputStream bos = new BufferedOutputStream(outputStream); OutputStreamWriter writer = new OutputStreamWriter(bos)) {
                for (Path file : files) {
                    writer.write("--" + boundary + "\r\n");
                    writer.write("Content-Type: application/octet-stream\r\n");
                    writer.write("Content-Disposition: attachment; filename=\"" + file.getFileName() + "\"\r\n\r\n");
                    writer.flush();
                    Files.copy(file, bos);
                    bos.write("\r\n".getBytes());
                    bos.flush();
                }
                writer.write("--" + boundary + "--\r\n");
                writer.flush();
            }
        };
    }

    @PostMapping(value = "/uploadflux", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Mono<String> uploadFileStreaming(@RequestPart("filePart") FilePart filePart) {
        return Mono.fromCallable(() -> {
            Path targetPath = UPLOAD_DIR.resolve(filePart.filename());
            Files.createDirectories(targetPath.getParent());
            return targetPath;
        }).flatMap(targetPath ->
                filePart.transferTo(targetPath)
                        .thenReturn("Upload successful: " + filePart.filename())
        );
    }

    @GetMapping(value = "/downloadflux", produces = "multipart/mixed")
    public ResponseEntity<Flux<DataBuffer>> downloadFiles() {
        String boundary = "filesBoundary";

        List<Path> files = List.of(
                UPLOAD_DIR.resolve("file1.txt"),
                UPLOAD_DIR.resolve("file2.txt")
        );

        // Use concatMap to ensure files are streamed one after another, sequentially.
        Flux<DataBuffer> fileFlux = Flux.fromIterable(files)
                .concatMap(file -> {
                    String partHeader = "--" + boundary + "\r\n" +
                            "Content-Type: application/octet-stream\r\n" +
                            "Content-Disposition: attachment; filename=\"" + file.getFileName() + "\"\r\n\r\n";

                    Flux<DataBuffer> fileContentFlux = DataBufferUtils.read(file, new DefaultDataBufferFactory(), 4096);
                    DataBuffer footerBuffer = new DefaultDataBufferFactory().wrap("\r\n".getBytes());

                    // Build the flux for this specific part: header + content + footer
                    return Flux.concat(
                            Flux.just(new DefaultDataBufferFactory().wrap(partHeader.getBytes())),
                            fileContentFlux,
                            Flux.just(footerBuffer)
                    );
                })
                // After all parts, concat the final boundary
                .concatWith(Flux.just(
                        new DefaultDataBufferFactory().wrap(("--" + boundary + "--\r\n").getBytes())
                ));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "multipart/mixed; boundary=" + boundary)
                .body(fileFlux);
    }
}
