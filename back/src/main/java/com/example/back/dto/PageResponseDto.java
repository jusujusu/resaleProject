package com.example.back.dto;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * PageResponseDto 설정
 *
 * @fileName : PageResponseDto
 * @since : 26. 3. 24.
 */

@Getter
@ToString
public class PageResponseDto<T> {

    private List<T> dtoList;      // 실제 화면에 보여줄 데이터 리스트 (Entity -> DTO 변환 후)
    private int pageNumber;       // 현재 페이지 번호 (사용자에게는 1부터 보여줌)
    private int pageSize;         // 한 페이지당 보여줄 게시글 개수
    private long totalElements;   // DB에 저장된 전체 데이터 개수
    private int totalPages;       // 전체 페이지 수 (마지막 페이지 번호)

    private boolean isFirst;      // 첫 번째 페이지 여부
    private boolean isLast;       // 마지막 페이지 여부
    private boolean hasNext;      // 다음 페이지 존재 여부
    private boolean hasPrevious;  // 이전 페이지 존재 여부


    /*
    * JPA의 Page 객체와 변환된 DTO 리스트를 받아 응답 객체를 생성
    * */
    public PageResponseDto(Page<?> page, List<T> dtoList) {
        this.dtoList = dtoList;

        // JPA는 페이지가 0부터 시작하므로, 사용자를 위해 +1 처리
        this.pageNumber = page.getNumber() + 1;

        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();

        // Spring Data Page가 기본 제공하는 메서드 활용
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }

}
