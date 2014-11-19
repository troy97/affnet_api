BEGIN;


-- CREATE TABLE "tbl_orders" -----------------------------------
CREATE TABLE "public"."tbl_orders" ( 
	"id" Bigserial NOT NULL, 
	"product_id" Bigint NOT NULL REFERENCES tbl_products ( id ) ON DELETE SET NULL,
	"distributor_id" Integer NOT NULL,
	"sub_id" Integer NOT NULL, 
	"click_id" Bigint NOT NULL,
	"status" Character Varying( 16 ) DEFAULT 'open' NOT NULL, 
	"price_original" Double Precision NOT NULL, 
	"currency_original_id" Integer NOT NULL REFERENCES tbl_currencies ( id ) ON DELETE SET NULL, 
	"price_common" Double Precision NOT NULL, 
	"title" Character Varying( 2044 ) NOT NULL, 
	"created_at" Bigint NOT NULL, 
	"updated_at" Bigint NOT NULL
 );
-- -------------------------------------------------------------;

COMMIT;
