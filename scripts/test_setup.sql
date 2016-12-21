 /**
  * Copyright 2016 - 29cu.io and the authors of alpha-umi open source project

  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at

  *     http://www.apache.org/licenses/LICENSE-2.0
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  **/


CREATE SCHEMA IF NOT EXISTS usmtestdb DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
GRANT ALL PRIVILEGES ON usmtestdb.* TO 'usm_user'@'localhost' IDENTIFIED BY 'usm_pass';
FLUSH PRIVILEGES;

DROP FUNCTION IF EXISTS fn_regexp_like; 
CREATE FUNCTION fn_regexp_like (value1 nvarchar(4000),value2 nvarchar(4000)) 
RETURNS CHAR(1) DETERMINISTIC 
RETURN value1 REGEXP value2;