INSERT INTO users (name, email, password, role, is_active, created_at)
VALUES (
           'Admin',
           'admin@gmail.com',
           '$2a$12$oQVo7FYvuv2S8tzYMDf2/.se/Q96shw.xmSHO66OeFnbsVeRhrNEC', --admin123
           'ROLE_ADMIN',
           TRUE,
           CURRENT_DATE
       );

COMMENT ON TABLE users IS 'Первый админ для входа в систему';