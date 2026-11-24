package com.spring.lab.security.jwt.controller;

import com.spring.lab.security.jwt.entity.BankCard;
import com.spring.lab.security.jwt.repository.BankCardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
public class CardRestController {

    private final BankCardRepository cardRepository;

    public CardRestController(BankCardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @GetMapping("/my-cards")
    @PreAuthorize("hasRole('USER') and (#email == authentication.name or hasRole('ADMIN'))")
    public ResponseEntity<List<BankCard>> getMyCards(@RequestParam("email") String email) {
        List<BankCard> cards = cardRepository.findByCustomerEmail(email);
        return ResponseEntity.ok(cards);
    }
}
