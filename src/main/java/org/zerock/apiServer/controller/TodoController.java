package org.zerock.apiServer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.zerock.apiServer.dto.PageRequestDTO;
import org.zerock.apiServer.dto.PageResponseDTO;
import org.zerock.apiServer.dto.TodoDTO;
import org.zerock.apiServer.service.TodoService;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;
    @GetMapping("/{tno}")
    public TodoDTO get(@PathVariable("tno") Long tno) {
        return todoService.get(tno);
    }

    @GetMapping("/list")
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {
        log.info("list.............." + pageRequestDTO);
        return todoService.getList(pageRequestDTO);
    }

    // JSON으로 return 보내야하니 Map이 그 역할을 한다.
    @PostMapping("/")
    public Map<String,Long> register(@RequestBody TodoDTO dto ) {
        log.info("todoDTO: " + dto);
        Long tno = todoService.register(dto);
        return Map.of("TNO" , tno);
    }

    @PutMapping("/{tno}")
    public Map<String, String> modify(@PathVariable("tno") Long tno, @RequestBody TodoDTO todoDTO) {
        todoDTO.setTno(tno);
        todoService.modify(todoDTO);
        return Map.of("RESULT", "success");
    }

    @DeleteMapping("/{tno}")
    public Map<String, String> remove(@PathVariable("tno") Long tno) {
        todoService.remove(tno);
        return Map.of("RESULT", "success");
    }
}
