CREATE TABLE IF NOT EXISTS bank_accounts(
  id VARCHAR(36) NOT NULL COMMENT 'ID'
  , balance INT(11) NOT NULL COMMENT '残高'
  , overdraft_limit INT(11) NOT NULL COMMENT '借り越し限度額'
  , created_by VARCHAR(50) NOT NULL COMMENT '登録者'
  , created_at DATETIME NOT NULL COMMENT '登録日時'
  , updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新者'
  , updated_at DATETIME DEFAULT NULL COMMENT '更新日時'
  , PRIMARY KEY (id)
) COMMENT='銀行口座';

CREATE TABLE IF NOT EXISTS bank_transactions(
  id VARCHAR(36) NOT NULL COMMENT 'ID'
  , transaction_type VARCHAR(50) NOT NULL COMMENT '取引区分'
  , amount INT(11) NOT NULL COMMENT '金額'
  , created_by VARCHAR(50) NOT NULL COMMENT '登録者'
  , created_at DATETIME NOT NULL COMMENT '登録日時'
  , updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新者'
  , updated_at DATETIME DEFAULT NULL COMMENT '更新日時'
  , PRIMARY KEY (id)
) COMMENT='銀行取引';

CREATE TABLE IF NOT EXISTS bank_transfers(
  id VARCHAR(36) NOT NULL COMMENT 'ID'
  , source_bank_account_id VARCHAR(36) NOT NULL COMMENT '送金元'
  , destination_bank_account_id VARCHAR(36) NOT NULL COMMENT '送金先'
  , amount INT(11) NOT NULL COMMENT '金額'
  , status VARCHAR(50) NOT NULL COMMENT 'ステータス'
  , message TEXT DEFAULT NULL COMMENT 'メッセージ'
  , created_by VARCHAR(50) NOT NULL COMMENT '登録者'
  , created_at DATETIME NOT NULL COMMENT '登録日時'
  , updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新者'
  , updated_at DATETIME DEFAULT NULL COMMENT '更新日時'
  , PRIMARY KEY (id)
) COMMENT='銀行振込';
