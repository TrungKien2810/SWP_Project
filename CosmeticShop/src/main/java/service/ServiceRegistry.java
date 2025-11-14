package service;

/**
 * Simple registry for application-wide services.
 * Allows swapping implementations in tests without touching servlet code.
 */
public final class ServiceRegistry {

    private static PromotionService promotionService = new DefaultPromotionService();
    private static CartPromotionService cartPromotionService = new DefaultCartPromotionService();

    private ServiceRegistry() {
    }

    public static PromotionService getPromotionService() {
        return promotionService;
    }

    public static void setPromotionService(PromotionService service) {
        promotionService = service;
    }

    public static CartPromotionService getCartPromotionService() {
        return cartPromotionService;
    }

    public static void setCartPromotionService(CartPromotionService service) {
        cartPromotionService = service;
    }
}

