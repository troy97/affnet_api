BEGIN;


-- CREATE FIELD "product_name" ---------------------------------
ALTER TABLE "public"."tbl_clicks" ADD COLUMN "product_name" Character Varying( 256 ) NOT NULL DEFAULT 'foo';
ALTER TABLE "public"."tbl_clicks" ALTER COLUMN "product_name" DROP DEFAULT;
-- -------------------------------------------------------------

-- CREATE FIELD "product_price" --------------------------------
ALTER TABLE "public"."tbl_clicks" ADD COLUMN "product_price" Double Precision NOT NULL DEFAULT -1.0;
ALTER TABLE "public"."tbl_clicks" ALTER COLUMN "product_price" DROP DEFAULT;
-- -------------------------------------------------------------

-- CREATE FIELD "shipping_price" -------------------------------
ALTER TABLE "public"."tbl_clicks" ADD COLUMN "shipping_price" Double Precision DEFAULT -1.0;
-- -------------------------------------------------------------;

COMMIT;
