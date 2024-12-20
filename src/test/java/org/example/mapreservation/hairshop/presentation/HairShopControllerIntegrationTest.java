package org.example.mapreservation.hairshop.presentation;

import org.example.mapreservation.common.Address;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.hairshop.infrastructure.HairShopJpaRepository;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SqlGroup({
        @Sql(value = "/sql/delete-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@AutoConfigureMockMvc
@SpringBootTest
class HairShopControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    HairShopJpaRepository hairShopJpaRepository;
    @Autowired
    OwnerRepository ownerRepository;

    @Test
    void 검색어와_페이지_정보를_전달하여_검색_결과를_받아볼_수_있다() throws Exception {
        // given - 데이터 준비
        Owner owner = new Owner("사장1");
        ownerRepository.save(owner);
        Address address = new Address("도로명 주소", "101호");
        hairShopJpaRepository.saveAll(List.of(
                HairShop.builder().name("헤어샵1").address(address).owner(owner).build(),
                HairShop.builder().name("헤어샵2").address(address).owner(owner).build(),
                HairShop.builder().name("헤어샵3").address(address).owner(owner).build(),
                HairShop.builder().name("헤어샵4").address(address).owner(owner).build(),
                HairShop.builder().name("헤어샵5").address(address).owner(owner).build(),
                HairShop.builder().name("블루클럽").address(address).owner(owner).build()
        ));

        // when, then
        mockMvc.perform(get("/api/hairshop")
                        .queryParam("searchTerm", "헤어")
                        .queryParam("page", "1")
                        .queryParam("size", "3")
                        .queryParam("sort", "name,desc")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(jsonPath("$.page.totalElements").value(5))
                .andExpect(jsonPath("$.page.totalPages").value(2))
                .andExpect(jsonPath("$.page.size").value(3))
                .andExpect(jsonPath("$.page.numberOfElements").value(2))
                .andExpect(jsonPath("$.page.content.size()").value(2))
                .andExpect(jsonPath("$.page.content[0].shopName").value("헤어샵2"))
                .andExpect(jsonPath("$.page.content[1].shopName").value("헤어샵1"))
                .andExpect(jsonPath("$.searchCondition.searchTerm").value("헤어"));
    }

    @Test
    void 특정_헤어샵의_상세_정보를_얻을_수_있다() throws Exception {
        // given - 데이터 준비
        Owner owner = new Owner("사장1");
        ownerRepository.save(owner);
        Address address = new Address("도로주소", "상세주소");
        HairShop hairShop = hairShopJpaRepository.save(
                HairShop.builder()
                        .name("헤어샵")
                        .address(address)
                        .owner(owner)
                        .longitude("10.0")
                        .latitude("20.0")
                        .imageUrls(List.of("url1", "url2", "url3"))
                        .build()
        );

        // when, then
        mockMvc.perform(get("/api/hairshops/{hairShopId}", hairShop.getId())
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shopId").isNotEmpty())
                .andExpect(jsonPath("$.shopName").value("헤어샵"))
                .andExpect(jsonPath("$.longitude").value("10.0"))
                .andExpect(jsonPath("$.latitude").value("20.0"))
                .andExpect(jsonPath("$.imageUrls").value(
                        Matchers.contains("url1", "url2", "url3")
                ))
                .andExpect(jsonPath("$.roadAddress").value("도로주소"))
                .andExpect(jsonPath("$.detailAddress").value("상세주소"));
    }
}
