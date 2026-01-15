TRUNCATE TABLE stores;

INSERT INTO stores (
  name,
  category_name,
  price,
  image_name,
  description,
  business_hours,
  regular_holiday,
  postal_code,
  address,
  phone_number
) VALUES
(
  'ラーメン太郎',
  'ラーメン',
  900,
  'ramen.jpeg',
  '濃厚豚骨ラーメンが自慢のお店です。',
  '11:00〜22:00',
  '水曜日',
  '460-0008',
  '名古屋市中区',
  '052-000-0000'
),
(
  '名古屋拉麺',
  'ラーメン',
  850,
  'miso-ramen.jpg',
  'あっさり醤油が人気の名古屋系ラーメン。',
  '10:30〜21:00',
  '木曜日',
  '461-0001',
  '名古屋市東区',
  '052-222-2222'
),
(
  '焼肉キング',
  '焼肉',
  3000,
  'yakiniku.jpeg',
  '国産和牛が楽しめる焼肉店',
  '17:00〜23:00',
  '月曜日',
  '368-7361',
  '名古屋市千種区',
  '052-111-1111'
);

-- roles（初期マスタなのでOK）
--INSERT INTO roles (id, name) VALUES
--(1, 'ROLE_GENERAL'),
--(2, 'ROLE_ADMIN');


-- 一般ユーザー
-- INSERT INTO users (
  --name,
  --furigana,
  --postal_code,
  --address,
  --phone_number,
  --email,
  --password,
  --role_id,
  --enabled
--) VALUES (
 -- '一般 太郎',
  --'イッパン タロウ',
 -- '460-0001',
 -- '名古屋市中区',
 -- '090-1111-1111',
  --'user@test.com',
 -- '$2a$10$myFiCQ356fKd4dDnb12TM.hkYI/xbuCHLuEmY8dSIDiIXZnLQq8y',
 -- 1,
 -- true
--); 



-- 管理者ユーザー

--INSERT INTO users (
  --name,
  --furigana,
  --postal_code,
  --address,
  --phone_number,
  --email,
  --password,
  --role_id,
  --enabled
--) VALUES (
  --'管理者 花子',
  --'カンリシャ ハナコ',
  --'460-0002',
  --'名古屋市東区',
  --'090-2222-2222',
  --'admin@test.com',
  --'$2a$10$myFiCQ356fKd4dDnb12TM.hkYI/xbuCHLuEmY8dSIDiIXZnLQq8y',
  --2,
  --true
--);
