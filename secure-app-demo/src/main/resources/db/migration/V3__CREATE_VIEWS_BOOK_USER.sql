CREATE OR REPLACE VIEW book_view AS
SELECT
id,
title,
author,
isbn,
price,
in_stock,
date_created,
CASE
        WHEN price < 10 THEN 'Budget'
        WHEN price < 25 THEN 'Standard'
        WHEN price < 50 THEN 'Premium'
        ELSE 'Luxury'
    END AS price_category,
EXTRACT(YEAR FROM date_created) AS year_created
FROM books;

CREATE OR REPLACE VIEW user_view AS
SELECT
u.id,
u.username,
u.email,
u.enabled,
u.date_created AS date_created,
COALESCE(
(
SELECT jsonb_agg(DISTINCT r.name)
            FROM user_roles ur
            JOIN roles r ON ur.role_id = r.id
            WHERE ur.user_id = u.id
),
'[]'::jsonb
) AS roles,
COALESCE(
(
SELECT jsonb_agg(DISTINCT a.name)
            FROM user_roles ur
            JOIN role_authorities ra ON ur.role_id = ra.role_id
            JOIN authorities a ON ra.authority_id = a.id
            WHERE ur.user_id = u.id
),
'[]'::jsonb
) AS authorities
FROM users u;