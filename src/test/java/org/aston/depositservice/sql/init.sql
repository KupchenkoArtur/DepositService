DELETE
FROM deposit_db.public.transactions;
DELETE
FROM deposit_db.public.deposit;
DELETE
FROM deposit_db.public.deposit_account;
DELETE
FROM deposit_db.public.percent;
DELETE
FROM deposit_db.public.product;

INSERT INTO product (id, name, currency, amount_min, amount_max, product_status, autoren_status, day_max, day_min,
                     time_limited, capitalization, replenishment, withdrawal, revocable, penalty)
VALUES ('fc5a8657-9574-4923-b8f5-04c5b7df88bb', 'Депозит стабильный', 'RUB', 10000, 1000000, true, true, 0, 30, false,
        0, true, 1, true, 0.05);

INSERT INTO percent (id, product_id, days_min, days_max, percent_rate)
VALUES ('042c7089-7e76-45cb-ac05-73362b720ffd', 'fc5a8657-9574-4923-b8f5-04c5b7df88bb', 30, 0, 0.5);

INSERT INTO deposit_account (id, main_num, perc_num, m_account_id, p_account_id)
VALUES ('043a8c25-393c-480e-9f25-75bdee8e0925', '12345678901234567890', null, 'f8f2e293-6685-439d-93db-9cf686fef4a5',
        'fbf6060e-1b92-48e6-aec5-74150f2c2c8d'),
       ('f0ba09da-00d4-48da-ae3a-b25449dbae40', '09876543210987654321', null, 'd1a17507-385f-45b7-b92f-ada3fe950802',
        'a3b6e0df-a9e4-4892-a0b1-40f0ebb3ccdd'),
       ('8082cabf-d297-42fe-98e0-47c947544323', '12345678900987654321', null, '92fd4ff3-b7c4-4f6c-9e9f-3327a48696ca',
        '88310f19-b4b1-4084-9474-b5f1b598169a');

INSERT INTO deposit (id, product_id, customer_id, account_id, initial_amount, start_date, end_date, close_date,
                     deposit_status, autoren_status, cur_percent)
VALUES ('4f852a90-0e96-475d-942a-08967e628237', 'fc5a8657-9574-4923-b8f5-04c5b7df88bb',
        '810b8291-216e-44fa-83fc-d41926af5981', '043a8c25-393c-480e-9f25-75bdee8e0925', 20000,
        '2024-01-01 00:00:00+00', '2025-01-01 00:00:00+00', null, true, true, 5),
       ('38cb3e8e-2df0-425c-aabc-a2c5e519b32c', 'fc5a8657-9574-4923-b8f5-04c5b7df88bb',
        '69b30753-f740-4e97-9228-9b6ebc8cf275', 'f0ba09da-00d4-48da-ae3a-b25449dbae40', 50000,
        '2023-01-01 00:00:00+00', '2025-01-01 00:00:00+00', null, true, true, 10);

INSERT INTO transactions (id, account_id, oper_date, oper_type, oper_sum, db_kt, perc_balance, cur_balance,
                          total_balance, deposit_id)
VALUES (gen_random_uuid(), '043a8c25-393c-480e-9f25-75bdee8e0925', '2024-01-01 00:00:00+00', 21, 20000, 2, 0, 20000, 0,
        '4f852a90-0e96-475d-942a-08967e628237'),
       (gen_random_uuid(), 'f0ba09da-00d4-48da-ae3a-b25449dbae40', '2024-01-01 00:00:00+00', 21, 50000, 2, 0, 50000, 0,
        '38cb3e8e-2df0-425c-aabc-a2c5e519b32c')