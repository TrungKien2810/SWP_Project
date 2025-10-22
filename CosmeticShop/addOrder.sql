Thêm 2 cột vào bảng Orders (để đối chiếu IPN)
ALTER TABLE Orders ADD vnp_txn_ref VARCHAR(32);
ALTER TABLE Orders ADD vnp_amount BIGINT;
--thêm db cho phương thức giao hàng
INSERT INTO ShippingMethods (method_id, name, description, cost, estimated_days, is_active)
VALUES
(1, N'Giao hàng tiết kiệm', N'Tiết kiệm chi phí, thời gian dự kiến 4–7 ngày', 15000.00, 5, 1),
(2, N'Giao hàng thường', N'Cân bằng chi phí và tốc độ, dự kiến 2–4 ngày', 30000.00, 3, 1),
(3, N'Giao hàng siêu tốc', N'Giao nhanh trong ngày hoặc 24h', 50000.00, 1, 1);