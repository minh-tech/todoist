package com.dwarves.todoist.controller;

import com.dwarves.todoist.Utils.Constant;
import com.dwarves.todoist.Utils.Utils;
import com.dwarves.todoist.model.Task;
import com.dwarves.todoist.service.TaskService;
import com.dwarves.todoist.service.TodoService;
import com.dwarves.todoist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("api/task")
@RestController
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;
    private final TodoService todoService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, TodoService todoService) {
        this.taskService = taskService;
        this.userService = userService;
        this.todoService = todoService;
    }

    @SuppressWarnings("unchecked")
    @PostMapping
    public ResponseEntity<?> assignUsers(@NonNull @RequestBody Map<String, Object> json) {
        List<Integer> userIdsJson;
        int todoIdJson = 0;

        try {
            userIdsJson = (List<Integer>) json.get(Constant.ASSIGNEE_IDS);
            todoIdJson = (int) json.get(Constant.TODOID);

            // Check a todoID is valid
            if (!Utils.isIdValid(todoIdJson)) {
                return ResponseEntity.badRequest().body(Constant.TODO_INVALID);
            }

            // Check a todoID exists in database
            List<Integer> todoIdsDatabase = todoService.getAllTodoIds();
            if (!Utils.isIdExisted(todoIdsDatabase, todoIdJson)) {
                return ResponseEntity.badRequest().body(Constant.TODO_NOT_FOUND);
            }

            // Check userIDs is valid
            if (!userIdsJson.stream().allMatch(Utils::isIdValid)) {
                return ResponseEntity.badRequest().body(Constant.USERIDS_INVALID);
            }

            // Check userIDs exist in database
            List<Integer> userIdsDatabase = userService.getAllUserIds();
            if (!userIdsJson.stream().allMatch(id -> Utils.isIdExisted(userIdsDatabase, id))) {
                return ResponseEntity.badRequest().body(Constant.USERS_NOT_FOUND);
            }

            taskService.assignUsers(userIdsJson, todoIdJson);
        } catch(ClassCastException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Constant.JSON_INCORRECT);
        } catch(DuplicateKeyException e) {
            return ResponseEntity.badRequest().body(Constant.ASSIGNMENT_DUPLICATED);
        }

        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<?> getAllAssignments() {
        return ResponseEntity.ok(taskService.getAllAssignments());
    }

    @PatchMapping
    public ResponseEntity<?> updateAssignment(@NonNull @RequestBody Task task) {

        // Check a assigneeID is valid
        if (!Utils.isIdValid(task.getAssigneeId())) {
            return ResponseEntity.badRequest().body(Constant.USER_INVALID);
        }

        // Check a userID exists in database
        List<Integer> userIdsDatabase = userService.getAllUserIds();
        if (!Utils.isIdExisted(userIdsDatabase, task.getAssigneeId())) {
            return ResponseEntity.badRequest().body(Constant.USER_NOT_FOUND);
        }

        // Check a todoID is valid
        if (!Utils.isIdValid(task.getTodoId())) {
            return ResponseEntity.badRequest().body(Constant.TODO_INVALID);
        }

        // Check a todoID exists in database
        List<Integer> todoIdsDatabase = todoService.getAllTodoIds();
        if (!Utils.isIdExisted(todoIdsDatabase, task.getTodoId())) {
            return ResponseEntity.badRequest().body(Constant.TODO_NOT_FOUND);
        }

        if (taskService.updateAssignment(task) == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Constant.ASSIGNMENT_NOT_FOUND);
        }
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
