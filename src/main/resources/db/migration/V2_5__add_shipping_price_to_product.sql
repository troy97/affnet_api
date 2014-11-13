BEGIN;


-- CREATE FIELD "shipping_price" -------------------------------
ALTER TABLE "public"."tbl_products" ADD COLUMN "shipping_price" Double Precision;
-- -------------------------------------------------------------;

COMMIT;