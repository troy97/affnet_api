BEGIN;


-- CREATE TABLE "tbl_synch_attempts" ---------------------------
CREATE TABLE "public"."tbl_synch_attempts" ( 
	"id" Serial NOT NULL UNIQUE, 
	"time_start" Bigint NOT NULL, 
	"time_stop" Bigint DEFAULT '0' NOT NULL, 
	"shop_source_id" Integer NOT NULL REFERENCES tbl_shop_sources ( id ) ON DELETE CASCADE ,
	"is_successful" Boolean NOT NULL, 
	"error_message" Character Varying( 128 )
 );
-- -------------------------------------------------------------;

COMMIT;
