INSERT INTO registration.cost_centers (id, external_id, version, created_on, last_update, name, description, active)
VALUES (998, '2bed30e7-419d-4097-841c-7d88fbc708ad', 0, current_timestamp, current_timestamp, 'Saúde e bem estar',
        'Centro de custo para coisas de saúde e bem estar', true);

INSERT INTO registration.movement_classes (id, external_id, version, created_on, last_update, active, name, type,
                                           description, budget, id_cost_center)
VALUES (998, 'ff8ac873-2cbd-43dd-a3e8-2bc451f4e3fa', 0, current_timestamp, current_timestamp, true, 'Academia',
        'EXPENSE', 'Despesas com academia', 720.00, 998);

INSERT INTO financial.recurring_movements (id, external_id, version, created_on, last_update, name, value, starting_at,
                                           state, auto_launch, indeterminate, total_quotes, starting_quote,
                                           current_quote, description)
VALUES (999, 'ba870b58-01aa-4477-8ea1-1c644a6770c4', 0, current_timestamp, current_timestamp,
        'Academia', 320.00, '2024-01-02', 'ACTIVE', true, true, NULL, NULL, NULL, 'Despesas com academia');

INSERT INTO financial.recurring_movements (id, external_id, version, created_on, last_update, name, value, starting_at,
                                           state, auto_launch, indeterminate, total_quotes, starting_quote,
                                           current_quote, description)
VALUES (998, '2afc2759-b38e-4e1a-9a56-3f54c11c7e5c', 0, current_timestamp, current_timestamp,
        'Pilates', 400.00, '2024-01-04', 'ENDED', true, true, NULL, NULL, NULL, 'Despesas pilates');

INSERT INTO financial.apportionments (external_id, version, created_on, last_update, value, id_movement_class,
                                      id_period_movement, id_recurring_movement)
VALUES ('7308f07e-83a7-4778-adee-e6d87f936682', 0, current_timestamp, current_timestamp,
        400.00, 998, NULL, 998);

INSERT INTO financial.apportionments (external_id, version, created_on, last_update, value, id_movement_class,
                                      id_period_movement, id_recurring_movement)
VALUES ('4d96a044-8e24-44f0-a834-36dc546bc44a', 0, current_timestamp, current_timestamp,
        320.00, 998, NULL, 999);
