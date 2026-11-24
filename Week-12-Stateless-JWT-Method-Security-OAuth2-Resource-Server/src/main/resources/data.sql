-- ==============================================================================
-- Initial Seed Data for ApexBank Stateless JWT & Method Security Architecture
-- ==============================================================================

-- 1. Bank Users (using DelegatingPasswordEncoder with {noop} prefix)
INSERT INTO bank_users (id, name, email, password, mobile_number, role, create_dt)
VALUES (1, 'Sarah Jenkins', 'user@apexbank.com', '{noop}ApexPass123!', '+1-555-0101', 'ROLE_USER', CURRENT_DATE);

INSERT INTO bank_users (id, name, email, password, mobile_number, role, create_dt)
VALUES (2, 'Marcus Vance', 'admin@apexbank.com', '{noop}ApexAdmin123!', '+1-555-0102', 'ROLE_ADMIN', CURRENT_DATE);

INSERT INTO bank_users (id, name, email, password, mobile_number, role, create_dt)
VALUES (3, 'David Kim', 'viewer@apexbank.com', '{noop}ApexViewer123!', '+1-555-0103', 'ROLE_VIEWER', CURRENT_DATE);

-- 2. Bank Authorities
INSERT INTO bank_authorities (id, name, user_id) VALUES (1, 'ROLE_USER', 1);
INSERT INTO bank_authorities (id, name, user_id) VALUES (2, 'ROLE_ADMIN', 2);
INSERT INTO bank_authorities (id, name, user_id) VALUES (3, 'ROLE_USER', 2);
INSERT INTO bank_authorities (id, name, user_id) VALUES (4, 'ROLE_VIEWER', 3);

-- 3. Bank Accounts
INSERT INTO bank_accounts (account_number, account_type, branch_address, balance, customer_email, create_dt)
VALUES (1865764534, 'Savings', '120 Wall Street, New York, NY', 245000.50, 'user@apexbank.com', CURRENT_DATE);

INSERT INTO bank_accounts (account_number, account_type, branch_address, balance, customer_email, create_dt)
VALUES (2984756201, 'Executive Checking', '450 Montgomery St, San Francisco, CA', 980000.00, 'admin@apexbank.com', CURRENT_DATE);

-- 4. Bank Cards
INSERT INTO bank_cards (card_id, card_number, customer_email, card_type, total_limit, amount_used, available_amount, create_dt)
VALUES (1, '4532-XXXX-XXXX-9812', 'user@apexbank.com', 'PLATINUM_VISA', 50000.0, 12500.0, 37500.0, CURRENT_DATE);

-- 5. Bank Loans
INSERT INTO bank_loans (loan_number, customer_email, start_dt, loan_type, total_loan, amount_paid, outstanding_amount, create_dt)
VALUES (101, 'user@apexbank.com', CURRENT_DATE, 'MORTGAGE', 500000.0, 50000.0, 450000.0, CURRENT_DATE);

-- 6. Public Notices
INSERT INTO public_notices (notice_id, notice_summary, notice_details, notic_beg_dt, notic_end_dt, create_dt)
VALUES (1, 'Stateless Security Gateway Migration', 'ApexBank core has transitioned to stateless JWT authentication with OAuth2 federation.', CURRENT_DATE - 1, CURRENT_DATE + 30, CURRENT_DATE);

-- 7. Bank Transactions
INSERT INTO bank_transactions (transaction_id, account_number, sender_email, transaction_dt, transaction_summary, transaction_type, transaction_amt, closing_balance)
VALUES ('TXN-9001', 1865764534, 'user@apexbank.com', CURRENT_DATE, 'Initial deposit transfer', 'CREDIT', 15000.0, 245000.50);
