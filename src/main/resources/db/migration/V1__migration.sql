CREATE TABLE public.tbl_admins ( 
	id                   serial  NOT NULL,
	name                 varchar(128)  ,
	password             varchar(64)  NOT NULL,
	email                varchar(256)  NOT NULL UNIQUE,
	CONSTRAINT pk_tbl_admins PRIMARY KEY ( id )
 ) ;

CREATE TABLE public.tbl_webshop_roles ( 
	id                   serial  NOT NULL,
	description          varchar(1024)  ,
	key                  varchar(32)  NOT NULL,
	CONSTRAINT pk_tbl_webshop_roles PRIMARY KEY ( id )
 ) ;

CREATE TABLE public.tbl_webshops ( 
	id                   serial  NOT NULL,
	name                 varchar(256)  NOT NULL,
	url                  varchar(256)  NOT NULL UNIQUE,
	CONSTRAINT pk_tbl_webshops PRIMARY KEY ( id )
 ) ;

CREATE TABLE public.tbl_files ( 
	id                   serial  NOT NULL,
	name                 varchar(128)  NOT NULL,
	fs_path              varchar(256)  NOT NULL UNIQUE,
	upload_time          bigint  NOT NULL,
	webshop_id           integer  NOT NULL,
	is_active            bool DEFAULT 'false' NOT NULL,
	is_valid             bool DEFAULT 'false' NOT NULL,
	products_count       bigint DEFAULT 0 ,
	file_size            bigint DEFAULT 0 ,
	compressed_file_size bigint DEFAULT 0 ,
	CONSTRAINT pk_tbl_files PRIMARY KEY ( id )
 ) ;

CREATE TABLE public.tbl_webshop_users ( 
	id                   serial NOT NULL,
	email                varchar(256)  NOT NULL UNIQUE,
	password_ssha256_hex varchar(64)  NOT NULL,
	created_at           timestamp  NOT NULL,
	is_active            bool DEFAULT 'false',
	name_first           varchar(128)  ,
	name_last            varchar(128)  ,
	webshop_id           integer NOT NULL,
	CONSTRAINT pk_tbl_webshop_users PRIMARY KEY ( id )
 ) ;


CREATE TABLE public.tbl_webshop_users_have_roles ( 
	webshop_user_id      integer  NOT NULL,
	webshop_role_id      integer  NOT NULL
 ) ;

ALTER TABLE public.tbl_files ADD CONSTRAINT fk_tbl_files FOREIGN KEY ( webshop_id ) REFERENCES public.tbl_webshops( id )    ;

ALTER TABLE public.tbl_webshop_users ADD CONSTRAINT fk_tbl_webshop_users FOREIGN KEY ( webshop_id ) REFERENCES public.tbl_webshops( id )    ;

ALTER TABLE public.tbl_webshop_users_have_roles ADD CONSTRAINT fk_tbl_webshop_users_have_roles FOREIGN KEY ( webshop_user_id ) REFERENCES public.tbl_webshop_users( id )    ;

ALTER TABLE public.tbl_webshop_users_have_roles ADD CONSTRAINT fk_tbl_webshop_users_have_roles_0 FOREIGN KEY ( webshop_role_id ) REFERENCES public.tbl_webshop_roles( id )    ;
