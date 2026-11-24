package com.spring.lab.security.jwt.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "bank_cards")
public class BankCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "card_number", nullable = false, length = 30)
    private String cardNumber;

    @Column(name = "customer_email", nullable = false, length = 120)
    private String customerEmail;

    @Column(name = "card_type", nullable = false, length = 50)
    private String cardType;

    @Column(name = "total_limit", nullable = false)
    private Double totalLimit;

    @Column(name = "amount_used", nullable = false)
    private Double amountUsed;

    @Column(name = "available_amount", nullable = false)
    private Double availableAmount;

    @Column(name = "create_dt", nullable = false)
    private LocalDate createDt;

    public BankCard() {
    }

    public BankCard(String cardNumber, String customerEmail, String cardType, Double totalLimit, Double amountUsed, Double availableAmount, LocalDate createDt) {
        this.cardNumber = cardNumber;
        this.customerEmail = customerEmail;
        this.cardType = cardType;
        this.totalLimit = totalLimit;
        this.amountUsed = amountUsed;
        this.availableAmount = availableAmount;
        this.createDt = createDt;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Double getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(Double totalLimit) {
        this.totalLimit = totalLimit;
    }

    public Double getAmountUsed() {
        return amountUsed;
    }

    public void setAmountUsed(Double amountUsed) {
        this.amountUsed = amountUsed;
    }

    public Double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public LocalDate getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDate createDt) {
        this.createDt = createDt;
    }
}
