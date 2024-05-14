INSERT INTO registration.wallets(external_id, "version", created_on, last_update, "name", "type", current_balance,
                                 active, description, bank, agency, "number")
VALUES ('d6421251-7b38-4765-88e0-4d70bc3bc4c7', 0, current_timestamp, NULL, 'Personal', 'PERSONAL', 0, true,
        'Personal wallet', NULL, NULL, NULL);

INSERT INTO registration.wallets(external_id, "version", created_on, last_update, "name", "type", current_balance,
                                 active, description, bank, agency, "number")
VALUES ('4ade8a17-460b-40fc-b200-1504bcd4aaf7', 0, current_timestamp, NULL, 'Investments', 'INVESTMENT', 0, true,
        'Investments account wallet', 'Broker', '321', '987654');

INSERT INTO registration.wallets(id, external_id, "version", created_on, last_update, "name", "type", current_balance,
                                 active, description, bank, agency, "number")
VALUES (999, 'cd00845c-ae27-47e4-8282-c8df1c42acfe', 0, current_timestamp, null, 'Bank account', 'BANK_ACCOUNT', 0,
        true, 'Bank account wallet', 'Bank', '123', '456789');
