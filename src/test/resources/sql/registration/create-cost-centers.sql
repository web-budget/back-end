INSERT INTO registration.cost_centers (id, external_id, version, created_on, last_update, name, full_name, description, active, expense_budget, income_budget)
VALUES (999, '52e3456b-1b0d-42c5-8be0-07ddaecce441', 0, current_timestamp, current_timestamp, 'Outros','Outros', 'Centro de custo geral', true, 10, 10);

INSERT INTO registration.cost_centers (id, external_id, version, created_on, last_update, active, name, full_name, expense_budget, income_budget)
VALUES(888, '3cb5732d-2551-4eb9-8b41-f5d312ba7aac', 0, current_timestamp, current_timestamp, true, 'Carro', 'Carro', 1000, 1000);

INSERT INTO registration.cost_centers (id, external_id, version, created_on, last_update, active, name, full_name, expense_budget)
VALUES(887, '99a9e2df-0980-4724-b9a4-bba7d8c12120', 0, current_timestamp, current_timestamp, true, 'Impostos', 'Carro > Impostos', 500);