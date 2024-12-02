INSERT INTO registration.cost_centers (id, external_id, version, created_on, last_update, name, description, active)
VALUES (998, '2bed30e7-419d-4097-841c-7d88fbc708ad', 0, current_timestamp, current_timestamp, 'Saúde e bem estar',
        'Centro de custo para coisas de saúde e bem estar', true);

INSERT INTO registration.movement_classes (id, external_id, version, created_on, last_update, active, name, type,
                                           description, budget, id_cost_center)
VALUES (998, 'ff8ac873-2cbd-43dd-a3e8-2bc451f4e3fa', 0, current_timestamp, current_timestamp, true, 'Mercado',
        'EXPENSE', 'Mensalidade academia', 320.00, 998);

INSERT INTO registration.financial_periods (id, external_id, "version", created_on, last_update, name, starting_at,
                                            ending_at, status, expenses_goal, revenues_goal)
VALUES (998, 'bc67ba91-7c0a-466d-877c-0b1fe2fb56bd', 0, current_timestamp, current_timestamp,
        '12/2024', '2024-12-01', '2024-12-31', 'ACTIVE', 4500.00, 9000.00);

INSERT INTO financial.period_movements (id, external_id, "version", created_on, last_update, name, due_date, value, state,
                                        quote_number, description, id_financial_period, id_payment,
                                        id_credit_card_invoice, id_recurring_movement)
VALUES (998, '287e26fa-763b-4efb-908e-734e637bb6fd', 0, current_timestamp, current_timestamp,
        'Academia', '2024-12-30', 320.00, 'OPEN', NULL, 'Despesas com a nova academia', 998, NULL, NULL, NULL);

INSERT INTO financial.apportionments (external_id, "version", created_on, last_update, value, id_movement_class,
                                      id_period_movement, id_recurring_movement)
VALUES ('131e3419-77d7-40e6-9a74-1073fe6f9e61', 0, current_timestamp, current_timestamp,
        320.00, 998, 998, NULL);

