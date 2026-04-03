package com.cafeorder.domain.menu.service;

import com.cafeorder.domain.menu.dto.PopularMenuResponse;
import com.cafeorder.domain.menu.repository.MenuRepository;
import com.cafeorder.domain.order.entity.OrderStatus;
import com.cafeorder.domain.order.repository.OrderItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("인기 메뉴는 최근 7일 COMPLETED 주문 기준으로 최대 3개를 반환한다")
    void getPopularMenus_returnsTop3WithinLast7Days() {
        // given
        List<PopularMenuResponse> stubResult = List.of(
                new PopularMenuResponse(1L, "Americano", 10L),
                new PopularMenuResponse(2L, "Latte",     7L),
                new PopularMenuResponse(3L, "Mocha",     5L)
        );
        when(orderItemRepository.findPopularMenusSince(
                eq(OrderStatus.COMPLETED),
                any(LocalDateTime.class),
                any(Pageable.class)
        )).thenReturn(stubResult);

        // when
        List<PopularMenuResponse> result = menuService.getPopularMenus();

        // then: 3개 반환, 첫 번째가 가장 인기 메뉴
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getMenuName()).isEqualTo("Americano");
        assertThat(result.get(0).getOrderCount()).isEqualTo(10L);
    }

    @Test
    @DisplayName("since 파라미터는 현재 시각 기준 7일 전 이후여야 한다")
    void getPopularMenus_sinceIsWithin7DaysFromNow() {
        // given
        when(orderItemRepository.findPopularMenusSince(any(), any(), any()))
                .thenReturn(List.of());

        LocalDateTime before = LocalDateTime.now().minusDays(7).minusSeconds(5);

        // when
        menuService.getPopularMenus();

        // then: since 파라미터가 7일 전 ~ 현재 사이에 있는지 검증
        ArgumentCaptor<LocalDateTime> sinceCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(orderItemRepository).findPopularMenusSince(
                eq(OrderStatus.COMPLETED),
                sinceCaptor.capture(),
                any(Pageable.class)
        );
        assertThat(sinceCaptor.getValue()).isAfter(before);
    }

    @Test
    @DisplayName("Pageable은 첫 페이지 최대 3개로 요청된다")
    void getPopularMenus_requestsPageSizeOf3() {
        // given
        when(orderItemRepository.findPopularMenusSince(any(), any(), any()))
                .thenReturn(List.of());

        // when
        menuService.getPopularMenus();

        // then
        ArgumentCaptor<Pageable> pageCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderItemRepository).findPopularMenusSince(
                eq(OrderStatus.COMPLETED),
                any(LocalDateTime.class),
                pageCaptor.capture()
        );
        assertThat(pageCaptor.getValue().getPageNumber()).isEqualTo(0);
        assertThat(pageCaptor.getValue().getPageSize()).isEqualTo(3);
    }

    @Test
    @DisplayName("최근 7일 주문이 없으면 빈 목록을 반환한다")
    void getPopularMenus_returnsEmptyWhenNoOrders() {
        // given
        when(orderItemRepository.findPopularMenusSince(any(), any(), any()))
                .thenReturn(List.of());

        // when
        List<PopularMenuResponse> result = menuService.getPopularMenus();

        // then
        assertThat(result).isEmpty();
    }
}

