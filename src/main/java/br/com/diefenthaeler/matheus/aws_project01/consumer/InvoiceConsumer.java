package br.com.diefenthaeler.matheus.aws_project01.consumer;

import br.com.diefenthaeler.matheus.aws_project01.model.Invoice;
import br.com.diefenthaeler.matheus.aws_project01.model.SnsMessage;
import br.com.diefenthaeler.matheus.aws_project01.repository.InvoiceRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceConsumer {

    private final ObjectMapper objectMapper;
    private final InvoiceRepository repository;
    private final AmazonS3 amazonS3;

    @JmsListener(destination = "${aws.sqs.queue.invoice.events.name}")
    public void receivedS3Event(TextMessage textMessage) throws JMSException, IOException {

        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);

        S3EventNotification s3EventNotification = objectMapper
                .readValue(snsMessage.getMessage(), S3EventNotification.class);

        processInvoiceNotification(s3EventNotification);
    }

    private void processInvoiceNotification(S3EventNotification s3EventNotification) throws IOException {
        for (S3EventNotification.S3EventNotificationRecord s3EventNotificationRecord : s3EventNotification.getRecords()) {
            S3EventNotification.S3Entity s3Entity = s3EventNotificationRecord.getS3();

            String bucketName = s3Entity.getBucket().getName();
            String objectKey = s3Entity.getObject().getKey();

            String invoiceFile = downloadOject(bucketName, objectKey);

            Invoice invoice = objectMapper.readValue(invoiceFile, Invoice.class);
            log.info("Invoice received: {}", invoice.getInvoiceNumber());

            repository.save(invoice);

            amazonS3.deleteObject(bucketName, objectKey);
        }
    }

    private String downloadOject(String bucketName, String objectKey) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, objectKey);

        StringBuilder sb = new StringBuilder();
        BufferedReader bf = new BufferedReader(
                new InputStreamReader(s3Object.getObjectContent()));
        String content = null;

        while ((content = bf.readLine()) != null) {
            sb.append(content);
        }
        return sb.toString();

    }

}
