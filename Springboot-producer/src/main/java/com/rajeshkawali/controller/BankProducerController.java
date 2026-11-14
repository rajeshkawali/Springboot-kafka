package com.rajeshkawali.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajeshkawali.dto.BankTransaction;
import com.rajeshkawali.service.BankProducerService;

@RestController
@RequestMapping("/kafka-producer")
public class BankProducerController {

	@Autowired
	private BankProducerService bankProducerService;

	@PostMapping("/send-money")
	public ResponseEntity<String> sendMoney(@RequestBody BankTransaction transaction) {
		String message = bankProducerService.sendMoney(transaction);
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}

}
