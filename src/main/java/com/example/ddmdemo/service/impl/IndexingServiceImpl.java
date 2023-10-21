package com.example.ddmdemo.service.impl;

import com.example.ddmdemo.exceptionhandling.exception.LoadingException;
import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.indexrepository.DummyIndexRepository;
import com.example.ddmdemo.service.interfaces.FileService;
import com.example.ddmdemo.service.interfaces.IndexingService;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {

    private final DummyIndexRepository dummyIndexRepository;

    private final FileService fileService;

    @Override
    public String indexDocument(MultipartFile documentFile) {
        var newIndex = new DummyIndex();
        newIndex.setTitle(
            Objects.requireNonNull(documentFile.getOriginalFilename()).split("\\.")[0]);
        newIndex.setContent(extractDocumentContent(documentFile));

        var serverFilename = fileService.store(documentFile, UUID.randomUUID().toString());
        newIndex.setServerFilename(serverFilename);

        dummyIndexRepository.save(newIndex);

        return serverFilename;
    }

    private String extractDocumentContent(MultipartFile multipartPdfFile) {
        String documentContent;
        try (var pdfFile = multipartPdfFile.getInputStream()) {
            var pdDocument = PDDocument.load(pdfFile);
            var textStripper = new PDFTextStripper();
            documentContent = textStripper.getText(pdDocument);
            pdDocument.close();
        } catch (IOException e) {
            throw new LoadingException("Error while trying to load PDF file content.");
        }
        return documentContent;
    }
}