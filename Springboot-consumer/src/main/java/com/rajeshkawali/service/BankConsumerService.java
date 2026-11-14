package com.rajeshkawali.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rajeshkawali.dto.BankTransaction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BankConsumerService {
	
	String transactionMessage;

	@KafkaListener(topics= "bank-topic" , groupId = "bank-group")
	public String receiveMoney(BankTransaction transaction) {
		log.info("Received transaction: {}", transaction);
		transactionMessage = "Bank transaction is completed -> " + transaction;
		return transactionMessage;
	}

	public String receiveMoney() {
		return transactionMessage;
	}

	public String getTransactionMessage() {
		return transactionMessage;
	}

}
