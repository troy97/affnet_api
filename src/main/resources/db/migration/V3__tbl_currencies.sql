BEGIN;


-- CREATE TABLE "tbl_currencies" -------------------------------
CREATE TABLE "public"."tbl_currencies" ( 
	"id" Serial UNIQUE NOT NULL, 
	"iso_code" Character Varying( 3 ) DEFAULT 'EUR' NOT NULL, 
	"title" Character Varying( 64 ) DEFAULT 'Euro' NOT NULL
 );
-- -------------------------------------------------------------;

COMMIT;
