package service;

import DAO.DiscountDB;
import Model.UserDiscountAssign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("PromotionService delegation tests")
class PromotionServiceTest {

    private DiscountDB discountDB;
    private PromotionService service;

    @BeforeEach
    void setUp() {
        discountDB = mock(DiscountDB.class);
        service = new DefaultPromotionService(discountDB);
    }

    @Test
    @DisplayName("assignDueForUser delegates to DiscountDB")
    void testAssignDueForUser() {
        service.assignDueForUser(10);
        verify(discountDB).assignDueForUser(10);
    }

    @Test
    @DisplayName("listAssignedDiscountsForUser returns DAO data")
    void testListAssignedDiscountsForUser() {
        List<UserDiscountAssign> expected = Collections.emptyList();
        when(discountDB.listAssignedDiscountsForUser(7)).thenReturn(expected);

        List<UserDiscountAssign> result = service.listAssignedDiscountsForUser(7);

        assertThat(result).isSameAs(expected);
    }

    @Test
    @DisplayName("createDiscount forwards all parameters")
    void testCreateDiscount() {
        when(discountDB.create(anyString(), anyString(), anyString(),
                anyDouble(), any(), any(), any(), any(), anyBoolean(),
                any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(true);

        boolean created = service.createDiscount("CODE", "Name", "PERCENTAGE", 10.0,
                100.0, 50.0, new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis() + 1000),
                true, "desc", 5, "ORDER_COUNT", 3.0, "cond", true, true, null);

        assertThat(created).isTrue();
        verify(discountDB).create(anyString(), anyString(), anyString(),
                anyDouble(), any(), any(), any(), any(), anyBoolean(),
                any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("updateDiscount forwards to DAO")
    void testUpdateDiscount() {
        when(discountDB.update(anyInt(), anyString(), anyString(), anyString(),
                anyDouble(), any(), any(), any(), any(), anyBoolean(),
                any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(true);

        boolean updated = service.updateDiscount(1, "CODE", "Name", "PERCENTAGE", 5.0,
                null, null, null, null, true,
                null, null, null, null, null, false, null, null);

        assertThat(updated).isTrue();
        verify(discountDB).update(anyInt(), anyString(), anyString(), anyString(),
                anyDouble(), any(), any(), any(), any(), anyBoolean(),
                any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("deleteDiscount returns DAO result")
    void testDeleteDiscount() {
        when(discountDB.delete(5)).thenReturn(true);
        assertThat(service.deleteDiscount(5)).isTrue();
        verify(discountDB).delete(5);
    }
}

