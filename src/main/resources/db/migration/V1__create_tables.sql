--
-- Service administrators table
--

CREATE TABLE tbl_admins (
    id SERIAL NOT NULL,
    email character varying(256) NOT NULL UNIQUE,
    password_ssha256_hex character varying(64) NOT NULL,
    name character varying(128),
    CONSTRAINT pk_tbl_admins PRIMARY KEY ( id )
);

--
-- Shops table
--

CREATE TABLE tbl_shops (
    id SERIAL NOT NULL,
    url character varying(256) NOT NULL UNIQUE,
    name character varying(256) NOT NULL,
    CONSTRAINT pk_tbl_shops PRIMARY KEY ( id )
);

--
-- Users of shops table
--

CREATE TABLE tbl_shop_users (
    id SERIAL NOT NULL,
    email character varying(256) NOT NULL UNIQUE,
    password_ssha256_hex character varying(64) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    is_active boolean DEFAULT false,
    name_first character varying(128),
    name_last character varying(128),
    shop_id integer NOT NULL REFERENCES tbl_shops ( id ) ON DELETE CASCADE,
    CONSTRAINT pk_tbl_shop_users PRIMARY KEY ( id )
);


--
-- Roles of shop users table
--

CREATE TABLE tbl_shop_user_roles (
    id SERIAL NOT NULL,
    description character varying(1024) NOT NULL,
    key character varying(32) NOT NULL UNIQUE,
    CONSTRAINT pk_tbl_shop_user_roles PRIMARY KEY ( id )
);

--
-- Table to link users with their roles
--

CREATE TABLE tbl_shop_users_have_roles (
    shop_user_id integer NOT NULL REFERENCES tbl_shop_users ( id ) MATCH FULL ON DELETE CASCADE,
    shop_user_role_id integer NOT NULL REFERENCES tbl_shop_user_roles ( id ) MATCH FULL ON DELETE CASCADE
);


--
-- Distributors table 
--

CREATE TABLE tbl_distributors (
    id SERIAL NOT NULL,
    email character varying(256) NOT NULL UNIQUE,
    password_ssha256_hex character varying(64) NOT NULL,
    name_first character varying(128) NOT NULL,
    name_last character varying(128) NOT NULL,
    CONSTRAINT pk_tbl_distributors PRIMARY KEY ( id )
);


--
-- Files uploaded by webshops table
--

CREATE TABLE tbl_files (
    id SERIAL NOT NULL,
    name character varying(128) NOT NULL,
    fs_path character varying(256) NOT NULL UNIQUE,
    upload_time bigint NOT NULL,
    shop_id integer NOT NULL REFERENCES tbl_shops ( id ) ON DELETE CASCADE, 
    is_active boolean DEFAULT false,
    is_valid boolean DEFAULT false,
    products_count bigint DEFAULT 0,
    file_size bigint DEFAULT 0,
    compressed_file_size bigint DEFAULT 0,
    CONSTRAINT pk_tbl_files PRIMARY KEY ( id )    
);
-- Index to prohibit inserting second active file (is_active == true)
--CREATE UNIQUE INDEX active_file_index ON tbl_files USING btree (is_active) WHERE is_active;
CREATE UNIQUE INDEX active_file_index ON tbl_files(is_active) WHERE is_active;

--
-- Products parsed from uploaded files 
--

CREATE TABLE tbl_products (
    id BIGSERIAL NOT NULL,
    url_path character varying(512) NOT NULL,
    name character varying(256) NOT NULL,
    description character varying(2044) NOT NULL,
    short_description character varying(2044),
    image_url character varying(2044),
    price double precision NOT NULL,
    price_currency character varying(32),
    weight integer,
    shipping_price double precision,
    category character varying(1024),
    ean character varying(16),
    shop_id integer NOT NULL REFERENCES tbl_shops ( id ) MATCH FULL ON DELETE CASCADE,
    file_id integer NOT NULL REFERENCES tbl_files ( id ) MATCH FULL ON DELETE CASCADE,
    CONSTRAINT pk_tbl_products PRIMARY KEY ( id )
);


--
-- Templates of files that used to create files for distributors
--

CREATE TABLE tbl_file_templates (
    id SERIAL NOT NULL,
    name character varying(128) NOT NULL,
    fs_path character varying(256) NOT NULL UNIQUE,
    is_active boolean DEFAULT false,
    products_count bigint DEFAULT 0,
    file_size bigint DEFAULT 0,
    compressed_file_size bigint DEFAULT 0,
    created_at bigint NOT NULL,
    shop_id integer NOT NULL REFERENCES tbl_shops ( id ) MATCH FULL ON DELETE CASCADE,
    file_id integer NOT NULL REFERENCES tbl_files ( id ) MATCH FULL ON DELETE CASCADE,
    CONSTRAINT pk_tbl_file_templates PRIMARY KEY ( id )
);



--
-- Requests (clicks) made on distributor links
--

CREATE TABLE tbl_clicks (
    id BIGSERIAL NOT NULL,
    product_id bigint NOT NULL REFERENCES tbl_products ( id ) MATCH FULL ON DELETE CASCADE,
    shop_id integer NOT NULL REFERENCES tbl_shops ( id ) MATCH FULL ON DELETE CASCADE,
    distributor_id integer NOT NULL REFERENCES tbl_distributors ( id ) MATCH FULL ON DELETE CASCADE,
    sub_id integer,
    CONSTRAINT pk_tbl_clicks PRIMARY KEY ( id )
);


