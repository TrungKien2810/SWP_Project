package Controller;

import Util.VnPayConfig;
import DAO.OrderDB;
import Model.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VnPayCreateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String orderIdStr = req.getParameter("orderId");
		if (orderIdStr == null) { resp.sendError(400, "Missing orderId"); return; }
		int orderId = Integer.parseInt(orderIdStr);

		long amountVnd;
		Order order;
		try {
			OrderDB odb = new OrderDB();
			order = odb.getById(orderId);
			if (order == null) { resp.sendError(404, "Order not found"); return; }
			amountVnd = Math.round(order.getTotalAmount());
		} catch (Exception e) {
			resp.sendError(500, "Cannot load order");
			return;
		}

		String txnRef = VnPayConfig.randomTxnRef(8);

		Map<String, String> vnp = new HashMap<>();
		vnp.put("vnp_Version",  "2.1.0");
		vnp.put("vnp_Command",  "pay");
		vnp.put("vnp_TmnCode",  VnPayConfig.vnp_TmnCode);
		vnp.put("vnp_Amount",   String.valueOf(amountVnd * 100));
		vnp.put("vnp_CurrCode", "VND");
		vnp.put("vnp_TxnRef",   txnRef);
		vnp.put("vnp_OrderInfo","Thanh toan don hang:" + orderId);
		vnp.put("vnp_OrderType","other");
		vnp.put("vnp_Locale",   Optional.ofNullable(req.getParameter("language")).orElse("vn"));
		vnp.put("vnp_ReturnUrl",VnPayConfig.vnp_ReturnUrl + "?orderId=" + orderId);
		String ip = VnPayConfig.getClientIp(req.getHeader("X-Forwarded-For"), req.getRemoteAddr());
		vnp.put("vnp_IpAddr",   ip);

		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
		vnp.put("vnp_CreateDate", fmt.format(cld.getTime()));
		cld.add(Calendar.MINUTE, 15);
		vnp.put("vnp_ExpireDate", fmt.format(cld.getTime()));

		try {
			OrderDB odb = new OrderDB();
			odb.attachVnpTxnRef(orderId, txnRef, amountVnd);
		} catch (Exception ignored) {}

		List<String> fieldNames = new ArrayList<>(vnp.keySet());
		Collections.sort(fieldNames);
		String signData = VnPayConfig.hashAllFields(vnp);
		String secureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, signData);

		StringBuilder query = new StringBuilder();
		for (int i = 0; i < fieldNames.size(); i++) {
			String k = fieldNames.get(i);
			String v = vnp.get(k);
			if (v != null && !v.isEmpty()) {
				query.append(VnPayConfig.urlEncode(k)).append('=').append(VnPayConfig.urlEncode(v));
				if (i < fieldNames.size() - 1) query.append('&');
			}
		}
		query.append("&vnp_SecureHash=").append(secureHash);

        resp.sendRedirect(VnPayConfig.vnp_PayUrl + "?" + query);
    }
}

