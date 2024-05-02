package org.zerock.apiServer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.apiServer.domain.Todo;
import org.zerock.apiServer.dto.TodoDTO;
import org.zerock.apiServer.repository.TodoRepository;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public TodoDTO get(Long tno) {
        Optional<Todo> res = todoRepository.findById(tno);
        Todo todo = res.orElseThrow();
        return entityToDTO(todo);
    }

    @Override
    public Long register(TodoDTO dto) {
        Todo todo = DTOToEntity(dto);
        Todo res = todoRepository.save(todo);
        return res.getTno();
    }

    @Override
    public void modify(TodoDTO dto) {
        Optional<Todo> res = todoRepository.findById(dto.getTno());
        Todo todo = res.orElseThrow();
        todo.setTitle(dto.getTitle());
        todo.setContent(dto.getContent());
        todo.setComplete(dto.isComplete());
        todo.setDueDate(dto.getDueDate());
        todoRepository.save(todo);
    }

    @Override
    public void remove(Long tno) {
        todoRepository.deleteById(tno);
    }
}
