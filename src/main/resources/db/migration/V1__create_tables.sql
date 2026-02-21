--
-- PostgreSQL database dump - FIXED VERSION
-- All ID columns now use BIGSERIAL for auto-increment
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';
SET default_table_access_method = heap;


CREATE TABLE public.about (
                              id BIGINT DEFAULT 1 PRIMARY KEY,
                              content TEXT,
                              updated_at DATE,
                              CONSTRAINT chk_about_single_row CHECK (id = 1)
);

COMMENT ON TABLE public.about IS 'Информация "О нас" (только одна запись с id=1)';


CREATE TABLE public.contacts (
                                 id BIGSERIAL PRIMARY KEY,
                                 address TEXT NOT NULL,
                                 email VARCHAR(255) NOT NULL,
                                 google_map_url VARCHAR(500),
                                 phone VARCHAR(20) NOT NULL,
                                 updated_at DATE
);

COMMENT ON TABLE public.contacts IS 'Контактная информация компании';


CREATE TABLE public.main_banner (
                                    id BIGSERIAL PRIMARY KEY,
                                    title VARCHAR(255) NOT NULL,
                                    subtitle VARCHAR(500),
                                    button_text VARCHAR(100),
                                    button_link VARCHAR(500),
                                    image_url VARCHAR(500),
                                    is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                    created_at DATE,
                                    updated_at DATE
);

CREATE INDEX idx_main_banner_is_active ON public.main_banner(is_active);

COMMENT ON TABLE public.main_banner IS 'Главный баннер на главной странице';


CREATE TABLE public.news (
                             id BIGSERIAL PRIMARY KEY,
                             title VARCHAR(255) NOT NULL,
                             description TEXT,
                             content TEXT NOT NULL,
                             image_url VARCHAR(500),
                             is_published BOOLEAN NOT NULL DEFAULT TRUE,
                             created_at DATE,
                             updated_at DATE
);

CREATE INDEX idx_news_created_at ON public.news(created_at DESC);
CREATE INDEX idx_news_is_published ON public.news(is_published);

COMMENT ON TABLE public.news IS 'Новости компании';


CREATE TABLE public.portfolio (
                                  id BIGSERIAL PRIMARY KEY,
                                  title VARCHAR(255) NOT NULL,
                                  description TEXT,
                                  image_url VARCHAR(500),
                                  project_url VARCHAR(500),
                                  is_published BOOLEAN NOT NULL DEFAULT TRUE,
                                  created_at DATE,
                                  updated_at DATE
);

CREATE INDEX idx_portfolio_is_published ON public.portfolio(is_published);
CREATE INDEX idx_portfolio_created_at ON public.portfolio(created_at DESC);

COMMENT ON TABLE public.portfolio IS 'Портфолио проектов';


CREATE TABLE public.services (
                                 id BIGSERIAL PRIMARY KEY,
                                 title VARCHAR(255) NOT NULL,
                                 description TEXT,
                                 price DECIMAL(10, 2),
                                 icon_url VARCHAR(500),
                                 is_published BOOLEAN NOT NULL DEFAULT TRUE,
                                 created_at DATE,
                                 updated_at DATE
);

CREATE INDEX idx_services_is_published ON public.services(is_published);

COMMENT ON TABLE public.services IS 'Услуги компании';


CREATE TABLE public.users (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(255) NOT NULL UNIQUE,
                              email VARCHAR(255) NOT NULL UNIQUE,
                              password VARCHAR(255) NOT NULL,
                              role VARCHAR(50) NOT NULL,
                              is_active BOOLEAN NOT NULL DEFAULT TRUE,
                              created_at DATE,
                              updated_at DATE,
                              CONSTRAINT users_role_check CHECK (role IN ('ROLE_ADMIN', 'ROLE_USER'))
);

CREATE INDEX idx_users_email ON public.users(email);
CREATE INDEX idx_users_name ON public.users(name);

COMMENT ON TABLE public.users IS 'Пользователи системы';


-- End of database dump