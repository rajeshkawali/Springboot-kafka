package com.rajeshkawali.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction {
	
    private String transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private String transactionType;
    private LocalDateTime timestamp;
    
}