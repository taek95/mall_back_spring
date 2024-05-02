package org.zerock.apiServer.repository.search;

import org.springframework.data.domain.Page;
import org.zerock.apiServer.domain.Todo;

// 동적 처리를 위해 QueryDSL 사용 , 의존성 추가로 QTodo 만들어 줬음
public interface TodoSearch {
    Page<Todo> search1();
}
