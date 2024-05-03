package org.zerock.apiServer.repository.search;

import org.springframework.data.domain.Page;
import org.zerock.apiServer.domain.Todo;
import org.zerock.apiServer.dto.PageRequestDTO;

// 동적 처리를 위해 QueryDSL 사용 , 의존성 추가로 QTodo 만들어 줬음
// 페이지 처리를 위한 것
public interface TodoSearch {
    Page<Todo> search1(PageRequestDTO pageRequestDTO);
}
