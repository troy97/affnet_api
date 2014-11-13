BEGIN;


-- CREATE TABLE "tbl_shop_sources" -----------------------------
CREATE TABLE "public"."tbl_shop_sources" ( 
	"id" Serial NOT NULL UNIQUE, 
	"shop_id" Integer NOT NULL REFERENCES tbl_shops ( id ) ON DELETE CASCADE, 
	"file_format" Character Varying( 128 ) NOT NULL, 
	"download_url" Character Varying( 512 ) NOT NULL, 
	"last_queried_at" Bigint DEFAULT '0' NOT NULL, 
	"is_active" Boolean DEFAULT 'false' NOT NULL, 
	"basic_http_auth_required" Boolean DEFAULT 'false' NOT NULL, 
	"basic_http_auth_username" Character Varying( 64 ), 
	"basic_http_auth_password" Character Varying( 64 )
 );
-- -------------------------------------------------------------;

COMMIT;
