package com.spring.lab.security.controller;

import com.spring.lab.security.entity.BankCard;
import com.spring.lab.security.repository.BankCardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
public class CardRestController {

    private final BankCardRepository bankCardRepository;

    public CardRestController(BankCardRepository bankCardRepository) {
        this.bankCardRepository = bankCardRepository;
    }

    @GetMapping("/my-cards")
    public ResponseEntity<List<BankCard>> getCardDetails(@RequestParam String email) {
        List<BankCard> cards = bankCardRepository.findByBankUserEmail(email);
        return ResponseEntity.ok(cards);
    }
}
