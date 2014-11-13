BEGIN;


-- CHANGE "LENGTH" OF "FIELD "category" ------------------------
ALTER TABLE "public"."tbl_products" ALTER COLUMN "category" TYPE Character Varying( 512 ) COLLATE "pg_catalog"."default";
-- -------------------------------------------------------------

-- CHANGE "NULLABLE" OF "FIELD "category" ----------------------
ALTER TABLE "public"."tbl_products" ALTER COLUMN "category" SET NOT NULL;
-- -------------------------------------------------------------

-- CHANGE "NAME" OF "FIELD "price_currency" --------------------
ALTER TABLE "public"."tbl_products" RENAME COLUMN "price_currency" TO "currency_code";
-- -------------------------------------------------------------

-- CHANGE "LENGTH" OF "FIELD "price_currency" ------------------
ALTER TABLE "public"."tbl_products" ALTER COLUMN "currency_code" TYPE Character Varying( 3 ) COLLATE "pg_catalog"."default";
-- -------------------------------------------------------------

-- CHANGE "NULLABLE" OF "FIELD "price_currency" ----------------
ALTER TABLE "public"."tbl_products" ALTER COLUMN "currency_code" SET NOT NULL;
-- -------------------------------------------------------------

-- CHANGE "NAME" OF "FIELD "url_path" --------------------------
ALTER TABLE "public"."tbl_products" RENAME COLUMN "url_path" TO "real_url";
-- -------------------------------------------------------------

-- CREATE FIELD "image_url" ------------------------------------
ALTER TABLE "public"."tbl_products" ADD COLUMN "image_url" Character Varying( 512 );
-- -------------------------------------------------------------

-- CREATE FIELD "description" ----------------------------------
ALTER TABLE "public"."tbl_products" ADD COLUMN "description" Character Varying( 2044 );
-- -------------------------------------------------------------

-- CREATE FIELD "description_short" ----------------------------
ALTER TABLE "public"."tbl_products" ADD COLUMN "description_short" Character Varying( 2044 );
-- -------------------------------------------------------------

-- CREATE FIELD "ean" ------------------------------------------
ALTER TABLE "public"."tbl_products" ADD COLUMN "ean" Character Varying( 13 );
-- -------------------------------------------------------------

-- DROP FIELD "model" ------------------------------------------
ALTER TABLE "public"."tbl_products" DROP COLUMN "model";
-- -------------------------------------------------------------

-- DROP FIELD "type" -------------------------------------------
ALTER TABLE "public"."tbl_products" DROP COLUMN "type";
-- -------------------------------------------------------------

-- DROP FIELD "vendor" -----------------------------------------
ALTER TABLE "public"."tbl_products" DROP COLUMN "vendor";
-- -------------------------------------------------------------;

COMMIT;
