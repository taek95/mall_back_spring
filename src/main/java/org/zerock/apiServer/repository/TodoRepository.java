package org.zerock.apiServer.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.apiServer.domain.Todo;
import org.zerock.apiServer.repository.search.TodoSearch;


public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {
}
