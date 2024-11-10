INSERT INTO account (id, name, balance) VALUES (1, 'Visa', 1000.00);
INSERT INTO account (id, name, balance) VALUES (2, 'Cash', 500.00);

INSERT INTO category (id, name) VALUES (1, 'Salary');
INSERT INTO category (id, name) VALUES (2, 'Food');

INSERT INTO entry (id, type, account_id, category_id, cost, datetime, description) VALUES (1, 'INCOME', 1, 1, 1000.00, '2024-09-10T16:01:00', 'First month salary');
INSERT INTO entry (id, type, account_id, category_id, cost, datetime, description) VALUES (2, 'OUTCOME', 2, 2, 100.00, '2024-09-11T16:01:00', 'Chicken and Eggs');