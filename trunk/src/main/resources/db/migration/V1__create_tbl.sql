CREATE TABLE tbl_admins ( 
	id                   serial  NOT NULL,
	name                 varchar(128)  ,
	password             varchar(64)  NOT NULL,
        email		     varchar(64)  NOT NULL UNIQUE,
	CONSTRAINT pk_tbl_admins PRIMARY KEY ( id )
);

CREATE TABLE tbl_webshops (
	id 		     serial NOT NULL,
	name		     varchar(256) NOT NULL,
	CONSTRAINT pk_tbl_webshops PRIMARY KEY (id)
);

CREATE TABLE tbl_files ( 
	id                   serial  NOT NULL,
	name                 varchar(128),
	fs_path              varchar(256)  NOT NULL UNIQUE,
	upload_time          bigint  NOT NULL,
	webshop_id	     int NOT NULL,
	CONSTRAINT pk_tbl_files PRIMARY KEY ( id ),
	CONSTRAINT fk_tbl_files FOREIGN KEY ( webshop_id ) REFERENCES tbl_webshops( id )
 );
