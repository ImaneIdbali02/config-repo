package com.ecommerce.order.infrastructure.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventConsumer.class);

    // Exemple de listener pour un topic générique
    @KafkaListener(topics = "${kafka.consumer.topic-name}", groupId = "${kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, String> record) {
        logger.info("Received message: Key = {}, Value = {}, Topic = {}, Partition = {}, Offset = {}",
                record.key(), record.value(), record.topic(), record.partition(), record.offset());
        // Ici, vous pouvez désérialiser le message et le traiter
    }

    // Vous pouvez ajouter d'autres listeners pour des topics spécifiques si nécessaire
    /*
    @KafkaListener(topics = "payment-events", groupId = "order-service-payment-group")
    public void listenPaymentEvents(ConsumerRecord<String, String> record) {
        logger.info("Received payment event: {}", record.value());
        // Traitement spécifique des événements de paiement
    }
    */
}

