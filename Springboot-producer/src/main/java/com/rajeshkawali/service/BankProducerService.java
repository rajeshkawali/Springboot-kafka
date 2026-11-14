package com.rajeshkawali.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rajeshkawali.dto.BankTransaction;

@Service
public class BankProducerService {

	@Autowired
	private KafkaTemplate<String, BankTransaction> kafkaTemplate;

	public String sendMoney(BankTransaction transaction) {
		kafkaTemplate.send("bank-topic", "bank-group", transaction);
		return "Bank transaction is completed";
	}

}
