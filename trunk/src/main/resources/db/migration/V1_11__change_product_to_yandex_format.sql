ALTER TABLE "public".tbl_products DROP COLUMN description;
ALTER TABLE "public".tbl_products DROP COLUMN short_description;
ALTER TABLE "public".tbl_products DROP COLUMN image_url;
ALTER TABLE "public".tbl_products DROP COLUMN weight;
ALTER TABLE "public".tbl_products DROP COLUMN shipping_price;
ALTER TABLE "public".tbl_products DROP COLUMN ean;

ALTER TABLE "public".tbl_products ADD "type" varchar(16) DEFAULT 'simplified' ;
ALTER TABLE "public".tbl_products ADD vendor varchar(64) DEFAULT 'unknown' ;
ALTER TABLE "public".tbl_products ADD model varchar(64) DEFAULT 'unknown' ;
