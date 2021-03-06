package com.dwarves.todoist.dao;

import com.dwarves.todoist.Utils.Constant;
import com.dwarves.todoist.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        final String sql = "SELECT * FROM user_table";
        return jdbcTemplate.query(sql, ((resultSet, i) -> new User(
                resultSet.getInt(Constant.USERID),
                resultSet.getString(Constant.USERNAME)
        )));
    }

    @Override
    public int addUser(User user) {
        final String sql = "INSERT INTO user_table (username) VALUES (?)";
        return jdbcTemplate.update(sql, user.getUsername());
    }

    @Override
    public List<Integer> getAllUserIds() {
        final String sql = "SELECT \"userId\" FROM user_table ORDER BY \"userId\" ASC";
        return jdbcTemplate.query(sql, ((resultSet, i) ->
                resultSet.getInt(Constant.USERID)
        ));
    }
}
