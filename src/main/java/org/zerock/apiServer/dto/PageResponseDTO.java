package org.zerock.apiServer.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// 페이징 결과물을 가져 오는 역할, 페이징 처리 로직
@Data
public class PageResponseDTO<E> {

    private List<E> dtoList;
    private List<Integer> pageNumList;
    private PageRequestDTO pageRequestDTO;
    private boolean prev, next;
    private int totalCount, prevPage, nextPage, totalPage, current;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(List<E> dtoList, PageRequestDTO pageRequestDTO, long total) {
        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = (int)total;

        // 끝페이지 end 계산 , pageRequestDTO.getPage() == 현재 페이지
        int end = (int)(Math.ceil(pageRequestDTO.getPage() / 10.0)) * 10;
        int start = end - 9;

        // 만약에 게시물이 8쪽 까지 밖에 없다면 8쪽이 마지막으로 뜨게 다시 작업
        // 만약 78개 있다면 7.8을 올림해서 8쪽까지 나오게 셋팅
        int last = (int)(Math.ceil(totalCount/(double)pageRequestDTO.getSize()));

        end = end > last ? last : end;
        this.prev = start > 1;
        this.next = totalCount > end * pageRequestDTO.getSize();

        // 시작부터 끝까지 boxed
        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        // 이전 페이지 번호는 있을 수도 없을 수도 있다.
        this.prevPage = prev ? start -1 : 0;
        this.nextPage = next ? end + 1 : 0;

    }


}
