-- 組織定義 (sample)
--------------
CREATE TABLE "organization" (
  "id"          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "location_id" VARCHAR(8)   NOT NULL,
  "chinese_name"        VARCHAR(255) NOT NULL,
  "phonetic_name"     VARCHAR(255) NOT NULL,
  "english_name"     VARCHAR(255) NOT NULL,
  "updated_at"  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  "created_at"  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

--  組織情報 (sample)
INSERT INTO "facility" ("location_id", "name", "address", "description") VALUES ('22100', '散布流一',  'さんぷるいち', 'sample1');
