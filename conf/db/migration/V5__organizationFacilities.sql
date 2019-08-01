-- organizationに紐付くfacility定義 (sample)
--------------
CREATE TABLE "organization_facilities" (
  "id"              INT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "organization_id" INT      NOT NULL,
  "facility_id"     INT      NOT NULL,
  "updated_at"  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  "created_at"  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
