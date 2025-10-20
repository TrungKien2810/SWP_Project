package Controller;

import Util.VnPayConfig;
import DAO.OrderDB;
import Model.Order;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

public class VnPayIpnServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json; charset=UTF-8");

		Map<String, String> fields = new HashMap<>();
		req.getParameterMap().forEach((k, v) -> fields.put(k, v[0]));
		String secureHash = fields.remove("vnp_SecureHash");
		fields.remove("vnp_SecureHashType");
		String signData = VnPayConfig.hashAllFields(fields);
		if (secureHash == null || !secureHash.equals(VnPayConfig.hmacSHA512(VnPayConfig.secretKey, signData))) {
			resp.getWriter().print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
			return;
		}

		String txnRef = fields.get("vnp_TxnRef");
		String rspCode = fields.get("vnp_ResponseCode");
		long amount    = Long.parseLong(fields.get("vnp_Amount")) / 100;

		try {
			OrderDB odb = new OrderDB();
			Order order = odb.findByVnpTxnRef(txnRef);
			if (order == null) { resp.getWriter().print("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}"); return; }
			if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
				resp.getWriter().print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}"); return;
			}
			if (Math.round(order.getTotalAmount()) != amount) {
				resp.getWriter().print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}"); return;
			}
			if ("00".equals(rspCode)) {
				odb.updatePaymentStatus(order.getOrderId(), "PAID");
			} else {
				odb.updatePaymentStatus(order.getOrderId(), "FAILED");
			}
			resp.getWriter().print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
		} catch (Exception e) {
			resp.getWriter().print("{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}");
		}
	}
}

