INSERT INTO registration.cards (external_id, version, created_on, last_update, active, name, last_four_digits,
                                invoice_payment_day, type,flag, id_wallet)
VALUES ('07a25a3d-ac5c-46b0-9174-24f69b2dc36c', 0, current_timestamp, current_timestamp, true,
        'Itau Personalite', '5431', 5, 'DEBIT', NULL, 999);
INSERT INTO registration.cards (external_id, version, created_on, last_update, active, name, last_four_digits,
                                invoice_payment_day, type,flag, id_wallet)
VALUES ('1dc82330-34c9-4f4a-b2ca-14fcd10c299f', 0, current_timestamp, current_timestamp, false,
        'XP Infinite', '1234', 8, 'CREDIT', 'Visa Infinite', NULL);