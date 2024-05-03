package org.zerock.apiServer.service;

import org.springframework.transaction.annotation.Transactional;
import org.zerock.apiServer.domain.Todo;
import org.zerock.apiServer.dto.PageRequestDTO;
import org.zerock.apiServer.dto.PageResponseDTO;
import org.zerock.apiServer.dto.TodoDTO;

// 객체와 DTO 전환을 여기서 한다.
@Transactional
public interface TodoService {
    // 리턴타입이 DTO
    TodoDTO get(Long tno);
    Long register(TodoDTO dto);
    void modify(TodoDTO dto);
    void remove(Long tno);

    // 페이지 처리 역할
    PageResponseDTO<TodoDTO> getList(PageRequestDTO pageRequestDTO);

//    java8 버전 이후로 이런게 나와서 추상클래스도 모호해짐
    default TodoDTO entityToDTO(Todo todo) {

        return  TodoDTO.builder()
                       .tno(todo.getTno())
                        .title(todo.getTitle())
                        .content(todo.getContent())
                        .complete(todo.isComplete())
                        .dueDate((todo.getDueDate()))
                        .build();

    }
    default Todo DTOToEntity(TodoDTO todoDTO) {

        return  Todo.builder()
                .tno(todoDTO.getTno())
                .title(todoDTO.getTitle())
                .content(todoDTO.getContent())
                .complete(todoDTO.isComplete())
                .dueDate((todoDTO.getDueDate()))
                .build();

    }
}
