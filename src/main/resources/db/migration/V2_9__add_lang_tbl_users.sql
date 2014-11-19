BEGIN;


-- CREATE FIELD "language" -------------------------------------
ALTER TABLE "public"."tbl_shop_users" ADD COLUMN "language" Character Varying( 10 ) DEFAULT 'en' NOT NULL;
-- -------------------------------------------------------------;

COMMIT;