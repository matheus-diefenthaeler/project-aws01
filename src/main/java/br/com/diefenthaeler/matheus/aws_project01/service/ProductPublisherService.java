package br.com.diefenthaeler.matheus.aws_project01.service;

import br.com.diefenthaeler.matheus.aws_project01.enums.EventType;
import br.com.diefenthaeler.matheus.aws_project01.model.Envelope;
import br.com.diefenthaeler.matheus.aws_project01.model.Product;
import br.com.diefenthaeler.matheus.aws_project01.model.ProductEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductPublisherService {

    private AmazonSNS snsClient;
    private Topic productEventsTopic;
    private ObjectMapper objectMapper;

    public ProductPublisherService(AmazonSNS snsClient,
                                   @Qualifier("productEventsTopic") Topic productEventsTopic,
                                   ObjectMapper objectMapper) {

        this.snsClient = snsClient;
        this.productEventsTopic = productEventsTopic;
        this.objectMapper = objectMapper;
    }

    public void publishProductEvent(Product product, EventType eventType, String username) {

        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductId(product.getId());
        productEvent.setCode(productEvent.getCode());
        productEvent.setUsername(username);

        Envelope envelope = new Envelope();

        envelope.setEventType(eventType);

        try {
            envelope.setData(objectMapper.writeValueAsString(productEvent));

            snsClient.publish(productEventsTopic.getTopicArn(), objectMapper.writeValueAsString(envelope));

        } catch (JsonProcessingException e) {
            log.error("Failed to create product event message");
        }
    }

}
