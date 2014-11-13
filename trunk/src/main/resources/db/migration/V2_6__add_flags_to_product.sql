BEGIN;

-- CREATE FIELD "is_active" ------------------------------------
ALTER TABLE "public"."tbl_products" ADD COLUMN "is_active" Boolean DEFAULT 'false' NOT NULL;
-- -------------------------------------------------------------

-- CREATE FIELD "is_processing" --------------------------------
ALTER TABLE "public"."tbl_products" ADD COLUMN "is_processing" Boolean DEFAULT 'false' NOT NULL;
-- -------------------------------------------------------------;

COMMIT;
