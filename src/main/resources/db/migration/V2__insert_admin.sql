INSERT INTO users (name, email, password, role, is_active, created_at)
VALUES (
           'Admin',
           'admin@aksoft.kg',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', --admin123
           'ROLE_ADMIN',
           TRUE,
           CURRENT_DATE
       );

COMMENT ON TABLE users IS 'Первый админ для входа в систему';