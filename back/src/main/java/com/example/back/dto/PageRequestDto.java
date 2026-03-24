package com.example.back.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


/**
 * PageRequestDto 설정
 *
 * @fileName : PageRequestDto
 * @since : 26. 3. 24.
 */

@Getter
@Setter
public class PageRequestDto {

    private int page = 1; // 사용자 입력 페이지 (1부터 시작)
    private int size = 10; // 한 페이지당 개수

    /**
     * JPA Pageable 객체로 변환
     * @param sortField 정렬 기준 필드명
     */
    public Pageable getPageable(String sortField) {
        // JPA는 내부적으로 0부터 시작하므로 page - 1 처리
        return PageRequest.of(page - 1, size, Sort.by(sortField).descending());
    }

}
