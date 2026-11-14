package com.rajeshkawali.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajeshkawali.service.BankConsumerService;

@RestController
@RequestMapping("/kafka-consumer")
public class BankConsumerController {

	@Autowired
	private BankConsumerService bankConsumerService;

	@GetMapping("/get-money")
	public ResponseEntity<String> sendMoney() {
		String message = bankConsumerService.receiveMoney();
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}

}
