package org.zerock.apiServer.repository.search;

import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.apiServer.domain.QTodo;
import org.zerock.apiServer.domain.Todo;

// queryDSL은 ~Impl로 이름을 맞추어 줘야 한다.
@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {

    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<Todo> search1() {

        log.info("search1........");
        // 쿼리를 날리기 위한 객체
        QTodo todo = QTodo.todo;
        // todo로 부터 뽑아낼거야
        JPQLQuery<Todo> query = from(todo);
        query.where(todo.title.contains("1"));
        Pageable pageable = PageRequest.of(1,10, Sort.by("tno").descending());
        this.getQuerydsl().applyPagination(pageable,query);
        //query 실행
        query.fetch();
        query.fetchCount();
        return null;
    }
}
