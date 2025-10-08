-- 1. Create table if not exists
CREATE TABLE IF NOT EXISTS commission_rules (
                                                id BIGSERIAL PRIMARY KEY,
                                                commission_name VARCHAR(100) NOT NULL,
    partner_tier VARCHAR(50),             -- e.g. GOLD, SILVER, BRONZE, or NULL
    category VARCHAR(50),                 -- e.g. ELECTRONICS, APPAREL, etc.
    min_amount DECIMAL(19,2),
    max_amount DECIMAL(19,2),
    percentage DECIMAL(5,2),
    fixed_bonus DECIMAL(19,2),
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP NOT NULL,
    priority INT NOT NULL
    );

-- 2. Tiered rules
INSERT INTO commission_rules
(commission_name, partner_tier, category, min_amount, max_amount, percentage, fixed_bonus, valid_from, valid_to, priority)
VALUES
    ('Tiered Base 0-10k', NULL, NULL, 0, 10000, 5.00, NULL, '2025-01-01 00:00:00', '2099-12-31 23:59:59', 1),
    ('Tiered Base 10k-50k', NULL, NULL, 10001, 50000, 7.00, NULL, '2025-01-01 00:00:00', '2099-12-31 23:59:59', 2),
    ('Tiered Base 50k+', NULL, NULL, 50001, NULL, 10.00, NULL, '2025-01-01 00:00:00', '2099-12-31 23:59:59', 3);

-- 3. Category-specific rules
INSERT INTO commission_rules
(commission_name, partner_tier, category, min_amount, max_amount, percentage, fixed_bonus, valid_from, valid_to, priority)
VALUES
    ('Luxury Category Bonus', NULL, 'LUXURY', NULL, NULL, 12.00, NULL, '2025-01-01 00:00:00', '2099-12-31 23:59:59', 10),
    ('Electronics Category', NULL, 'ELECTRONICS', NULL, NULL, 3.00, NULL, '2025-01-01 00:00:00', '2099-12-31 23:59:59', 11),
    ('Apparel Category', NULL, 'APPAREL', NULL, NULL, 8.00, NULL, '2025-01-01 00:00:00', '2099-12-31 23:59:59', 12);

-- 4. Seasonal rules (e.g. Black Friday week)
INSERT INTO commission_rules
(commission_name, partner_tier, category, min_amount, max_amount, percentage, fixed_bonus, valid_from, valid_to, priority)
VALUES
    ('Black Friday Bonus', NULL, NULL, NULL, NULL, 5.00, NULL, '2025-11-20 00:00:00', '2025-11-30 23:59:59', 20);