package Util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VnPayConfig {

	public static String vnp_PayUrl    = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
	public static String vnp_ReturnUrl = "http://localhost:8080/CosmeticShop-1.0-SNAPSHOT/payment/vnpay/return";
	public static String vnp_TmnCode   = "QWD4QKWJ";
	public static String secretKey     = "T96WC2PFRFPJGXYIXCWZXIGI5O3676HK";
	public static String vnp_ApiUrl    = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

	public static String hmacSHA512(String key, String data) {
		try {
			Mac mac = Mac.getInstance("HmacSHA512");
			mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
			byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static String hashAllFields(Map<String, String> fields) {
		List<String> names = new ArrayList<>(fields.keySet());
		Collections.sort(names);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < names.size(); i++) {
			String k = names.get(i);
			String v = fields.get(k);
			if (v != null && !v.isEmpty()) {
				sb.append(k).append("=").append(urlEncode(v));
				if (i < names.size() - 1) sb.append("&");
			}
		}
		return sb.toString();
	}

	public static String urlEncode(String s) {
		return URLEncoder.encode(s, StandardCharsets.US_ASCII);
	}

	public static String getClientIp(String xForwardedFor, String remoteAddr) {
		String ip = (xForwardedFor != null && !xForwardedFor.isEmpty()) ? xForwardedFor : remoteAddr;
		if (ip == null) return "";
		int comma = ip.indexOf(',');
		return comma > 0 ? ip.substring(0, comma).trim() : ip.trim();
	}

	public static String randomTxnRef(int len) {
		String digits = "0123456789";
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) sb.append(digits.charAt(r.nextInt(digits.length())));
		return sb.toString();
	}

	public static String describeResponseCode(String code) {
		if (code == null) return "Lỗi không xác định";
		switch (code) {
			case "00": return "Giao dịch thành công";
			case "07": return "Trừ tiền thành công nhưng giao dịch bị nghi ngờ";
			case "09": return "Thẻ/Tài khoản chưa đăng ký InternetBanking";
			case "10": return "Xác thực thẻ/tài khoản sai quá 3 lần";
			case "11": return "Hết hạn chờ thanh toán";
			case "12": return "Thẻ/Tài khoản bị khóa";
			case "13": return "Nhập sai OTP";
			case "24": return "Khách hàng hủy giao dịch";
			case "51": return "Không đủ số dư";
			case "65": return "Vượt hạn mức giao dịch trong ngày";
			case "75": return "Ngân hàng thanh toán đang bảo trì";
			case "79": return "Nhập sai mật khẩu thanh toán quá số lần quy định";
			case "99": return "Lỗi khác";
			default: return "Lỗi không xác định";
		}
	}

	public static String statusForResponseCode(String code) {
		return "00".equals(code) ? "PAID" : ("07".equals(code) ? "REVIEW" : "FAILED");
	}
}

