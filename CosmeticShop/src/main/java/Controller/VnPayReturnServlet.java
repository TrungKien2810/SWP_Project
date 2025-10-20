package Controller;

import Util.VnPayConfig;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

public class VnPayReturnServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, String> fields = new HashMap<>();
		req.getParameterMap().forEach((k, v) -> fields.put(k, v[0]));
		String vnp_SecureHash = fields.remove("vnp_SecureHash");
		fields.remove("vnp_SecureHashType");

		String signData = VnPayConfig.hashAllFields(fields);
		String signed = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, signData);

		String orderIdStr = req.getParameter("orderId");
		String rsp = req.getParameter("vnp_ResponseCode");
		boolean ok = signed != null && signed.equals(vnp_SecureHash);

		if (ok && "00".equals(rsp)) {
			resp.sendRedirect(req.getContextPath() + "/View/order-confirmation.jsp?status=success&orderId=" + orderIdStr);
		} else {
			resp.sendRedirect(req.getContextPath() + "/View/checkout.jsp?error=payment");
		}
	}
}

