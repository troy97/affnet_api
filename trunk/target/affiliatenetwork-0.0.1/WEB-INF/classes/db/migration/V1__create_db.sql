CREATE TABLE tbl_users ( 
	id                   bigserial  NOT NULL,
	name                 varchar(128)  ,
	login                varchar(64)  NOT NULL UNIQUE,
	password             varchar(64)  NOT NULL,
        email		     varchar(64)  NOT NULL,
	CONSTRAINT pk_tbl_users PRIMARY KEY ( id )
 );

CREATE TABLE tbl_csvs ( 
	id                   bigserial  NOT NULL,
	name                 varchar(128)  ,
	fs_path              varchar(256)  NOT NULL UNIQUE,
	upload_time          timestamp  NOT NULL,
	user_id		     bigint NOT NULL,
	CONSTRAINT pk_tbl_csvs PRIMARY KEY ( id ),
	CONSTRAINT fk_tbl_csvs FOREIGN KEY ( user_id ) REFERENCES tbl_users( id )
 );
