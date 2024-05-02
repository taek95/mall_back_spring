package org.zerock.apiServer.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.apiServer.domain.Todo;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@Log4j2
public class TodoRepositoryTests {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    public void test1() {
        Assertions.assertNotNull(todoRepository);
        log.info(todoRepository.getClass().getName());
    }

    @Test
    public void testInsert() {
        Todo todo = Todo.builder()
                .title("Title")
                .content("Content...")
                .dueDate(LocalDate.of(2023,12,30))
                .build();

        Todo result = todoRepository.save(todo);
        log.info(result);
    }

    @Test
    public void testRead() {
        Long tno = 1L;
        Optional<Todo> result = todoRepository.findById(tno);
        // orElseThrow : 문제 있으면 예외 던져 없으면 주고
        Todo todo = result.orElseThrow();
        log.info(todo);
    }

    @Test
    public void testUpdate() {
        // 먼저 로딩 하고 엔티티 객체를 변경
        Long tno = 1L;
        Optional<Todo> res = todoRepository.findById(tno);
        Todo todo = res.orElseThrow();
        todo.setTitle("Update Title");
        todo.setContent("updated content");
        todo.setComplete(true);
        todoRepository.save(todo);
    }

    @Test
    public void testPaging() {
        // 페이지 번호는 0부터 시작
        Pageable pageable = PageRequest.of(0,10,Sort.by("tno").descending());
        Page<Todo> res = todoRepository.findAll(pageable);
        log.info(res.getTotalElements());
        log.info(res.getContent());

    }

    @Test
    public void testSearch1() {
        todoRepository.search1();
    }
}
