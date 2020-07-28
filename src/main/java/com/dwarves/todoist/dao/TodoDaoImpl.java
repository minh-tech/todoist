package com.dwarves.todoist.dao;

import com.dwarves.todoist.Utils.Constant;
import com.dwarves.todoist.Utils.Utils;
import com.dwarves.todoist.model.Todo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class TodoDaoImpl implements TodoDao{

    private final JdbcTemplate jdbcTemplate;

    public TodoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Todo> getAllTodo() {
        final String sql = "SELECT * FROM todo_table";
        return jdbcTemplate.query(sql, ((resultSet, i) -> new Todo(
                resultSet.getInt(Constant.TODOID),
                resultSet.getString(Constant.CONTENT),
                Utils.convertDateToString(resultSet.getDate(Constant.COMPLETE_DATE), Constant.PATTERN)
        )));
    }

    @Override
    public int insertTodo(Todo todo) {
        final String sql = "INSERT INTO todo_table (content, complete_date) VALUES (?, ?)";
        Date date = Utils.convertStringToDate(todo.getComplete_date(), Constant.PATTERN);
        return jdbcTemplate.update(sql, todo.getContent(), date);
    }

    @Override
    public int updateTodoById(Todo todo) {
        final String sql = "UPDATE todo_table SET content = ?, complete_date = ? WHERE \"todoId\" = ?";
        return jdbcTemplate.update(sql, todo.getContent(), todo.getComplete_date(), todo.getTodoId());
    }

    @Override
    public List<Todo> getTodoByDate(Date date) {
        final String sql = "SELECT * FROM todo_table WHERE complete_date = ?";

        return jdbcTemplate.query(sql, new Object[]{date}, ((resultSet, i) -> new Todo(
                resultSet.getInt(Constant.TODOID),
                resultSet.getString(Constant.CONTENT),
                Utils.convertDateToString(resultSet.getDate(Constant.COMPLETE_DATE), Constant.PATTERN)
        )));
    }
}
