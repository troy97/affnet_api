BEGIN;


-- CREATE FIELD "auth_login" -----------------------------------
ALTER TABLE "public"."tbl_shops" ADD COLUMN "auth_login" Character Varying( 64 );
-- -------------------------------------------------------------

-- CREATE FIELD "auth_password" --------------------------------
ALTER TABLE "public"."tbl_shops" ADD COLUMN "auth_password" Character Varying( 64 );
-- -------------------------------------------------------------;

COMMIT;
