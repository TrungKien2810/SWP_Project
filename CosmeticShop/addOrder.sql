Thêm 2 cột vào bảng Orders (để đối chiếu IPN)
ALTER TABLE Orders ADD vnp_txn_ref VARCHAR(32);
ALTER TABLE Orders ADD vnp_amount BIGINT;