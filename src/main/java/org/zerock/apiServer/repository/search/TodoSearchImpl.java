package org.zerock.apiServer.repository.search;

import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.apiServer.domain.QTodo;
import org.zerock.apiServer.domain.Todo;
import org.zerock.apiServer.dto.PageRequestDTO;

import java.util.List;

// queryDSL은 ~Impl로 이름을 맞추어 줘야 한다.
@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {

    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<Todo> search1(PageRequestDTO pageRequestDTO) {

        log.info("search1........");
        // 쿼리를 날리기 위한 객체
        QTodo todo = QTodo.todo;
        // todo로 부터 뽑아낼거야
        JPQLQuery<Todo> query = from(todo);

        // pageRequestDTO.getPage() 는 1부터 시작하고 pageable은 0부터 시작
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage()-1,
                pageRequestDTO.getSize(),
                Sort.by("tno").descending());

        this.getQuerydsl().applyPagination(pageable,query);

        //query 실행, list로 받아오고, total count 알 수 있다.
        List<Todo> list = query.fetch();
        long total = query.fetchCount();

        // 페이징 처리 방법
        return new PageImpl<>(list, pageable, total);
    }
}
