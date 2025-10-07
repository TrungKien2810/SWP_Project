<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Quên mật khẩu</title>
  </head>
  <body>
    <h3>Khôi phục mật khẩu</h3>
    <form method="post" action="${pageContext.request.contextPath}/password/reset/request">
      <label>Nhập email của bạn</label>
      <input type="email" name="email" required>
      <button type="submit">Gửi link khôi phục</button>
    </form>
  </body>
</html>



