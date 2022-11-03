package br.com.diefenthaeler.matheus.aws_project01.controller;

import br.com.diefenthaeler.matheus.aws_project01.model.Invoice;
import br.com.diefenthaeler.matheus.aws_project01.model.UrlResponse;
import br.com.diefenthaeler.matheus.aws_project01.repository.InvoiceRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Value("${aws.s3.bucket.invoice.name}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    private final InvoiceRepository invoiceRepository;


    @PostMapping
    public ResponseEntity<UrlResponse> createInvoiceUrl() {
        UrlResponse urlResponse = new UrlResponse();
        Instant expirationTime = Instant.now().plus(Duration.ofMinutes(5));
        String processId = UUID.randomUUID().toString();

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, processId)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(Date.from(expirationTime));

        urlResponse.setExpirationTime(expirationTime.getEpochSecond());
        urlResponse.setUrl(amazonS3.generatePresignedUrl(
                generatePresignedUrlRequest).toString());

        return new ResponseEntity<UrlResponse>(urlResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Iterable<Invoice>> findAll() {
        Iterable<Invoice> all = invoiceRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{customerName}")
    public ResponseEntity<Iterable<Invoice>> findByCustomerName(@RequestParam String customerName) {
        Iterable<Invoice> allByCustomerName = invoiceRepository.findAllByCustomerName(customerName);
        return ResponseEntity.ok(allByCustomerName);
    }
}
