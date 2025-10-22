package Util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CartCookieUtil {

	public static final String GUEST_CART_COOKIE = "guest_cart";

	public static Map<Integer, Integer> readCartMap(HttpServletRequest request) {
		Map<Integer, Integer> map = new HashMap<>();
		Cookie cookie = getCookie(request, GUEST_CART_COOKIE);
		if (cookie == null) return map;
		String value = cookie.getValue();
		if (value == null || value.isEmpty()) return map;
		// Format: id:qty|id:qty
		String[] items = value.split("\\|");
		for (String item : items) {
			if (item == null || item.isEmpty()) continue;
			String[] parts = item.split(":");
			if (parts.length != 2) continue;
			try {
				int id = Integer.parseInt(parts[0]);
				int qty = Integer.parseInt(parts[1]);
				if (qty > 0) {
					map.put(id, map.getOrDefault(id, 0) + qty);
				}
			} catch (NumberFormatException ignore) {
				// ignore invalid entry
			}
		}
		return map;
	}

	public static void writeCartMap(HttpServletResponse response, Map<Integer, Integer> map) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Map.Entry<Integer, Integer> e : map.entrySet()) {
			if (e.getValue() == null || e.getValue() <= 0) continue;
			if (!first) sb.append('|');
			first = false;
			sb.append(e.getKey()).append(':').append(e.getValue());
		}
		Cookie cookie = new Cookie(GUEST_CART_COOKIE, sb.toString());
		cookie.setPath("/");
		cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
		response.addCookie(cookie);
	}

	public static void clearCartCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie(GUEST_CART_COOKIE, "");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) return null;
		for (Cookie c : cookies) {
			if (c.getName().equals(name)) return c;
		}
		return null;
	}
}


