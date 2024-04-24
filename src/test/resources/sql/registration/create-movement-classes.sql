INSERT INTO registration.movement_classes (external_id, version, created_on, last_update, active, name, type,
                                           description, budget, id_cost_center)
VALUES ('f21d94d2-d28e-4aa3-b12d-8a520023edd9', 0, current_timestamp, current_timestamp, true, 'Mercado', 'EXPENSE',
        'Despesas no mercado', 2000.00, 999);

INSERT INTO registration.movement_classes (external_id, version, created_on, last_update, active, name, type,
                                           description, budget, id_cost_center)
VALUES ('686f3fa3-6a08-4a5a-8587-d27b94a64097', 0, current_timestamp, current_timestamp, true, 'Conta de Luz',
        'EXPENSE',
        'Despesas com casa, conta de luz', 500, 999);

INSERT INTO registration.movement_classes (external_id, version, created_on, last_update, active, name, type,
                                           description, budget, id_cost_center)
VALUES ('98cb4961-5cde-46fb-abfd-8461be7d628b', 0, current_timestamp, current_timestamp, true, 'Vendas', 'INCOME',
        'Receita com vendas', 3500, 999);